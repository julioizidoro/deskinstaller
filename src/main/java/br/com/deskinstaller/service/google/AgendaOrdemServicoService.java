package br.com.deskinstaller.service.google;

import br.com.deskinstaller.config.GoogleCalendarProperties;
import br.com.deskinstaller.model.Cliente;
import br.com.deskinstaller.model.Endereco;
import br.com.deskinstaller.model.Funcionario;
import br.com.deskinstaller.model.Ordemservico;
import br.com.deskinstaller.model.OsFuncionario;
import br.com.deskinstaller.model.Relservico;
import br.com.deskinstaller.repository.OsFuncionarioRepository;
import br.com.deskinstaller.repository.RelServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Espelha ordens de servico como eventos em uma agenda unica da empresa.
 *
 * <p>Regra de ouro: a agenda e um efeito colateral do negocio, nunca o
 * contrario. Toda falha de integracao e registrada em log e engolida, para que
 * uma indisponibilidade do Google jamais impeca alguem de salvar uma OS.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AgendaOrdemServicoService {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter RFC3339_LOCAL = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final LocalTime HORA_PADRAO = LocalTime.of(8, 0);

    private final GoogleCalendarProperties propriedades;
    private final GoogleCalendarClient calendarClient;
    private final OsFuncionarioRepository osFuncionarioRepository;
    private final RelServicoRepository relServicoRepository;

    /** Duracao presumida do atendimento, em minutos. */
    @Value("${google.calendar.duracao-padrao-minutos:60}")
    private int duracaoPadraoMinutos;

    /** Antecedencia do lembrete enviado aos participantes, em minutos. */
    @Value("${google.calendar.lembrete-minutos:60}")
    private int lembreteMinutos;

    /** Se falso, o Google nao dispara e-mails de convite (util em homologacao). */
    @Value("${google.calendar.notificar-convidados:true}")
    private boolean notificarConvidados;

    /**
     * Cria ou atualiza o evento da ordem de servico.
     *
     * @return o id do evento no Google, ou o id anterior quando nada mudou;
     *         {@code null} se a integracao estiver desligada ou falhar
     */
    public String sincronizar(Ordemservico ordem) {
        if (!integracaoAtiva() || ordem == null) {
            return ordem != null ? ordem.getGoogleEventId() : null;
        }

        // Ordens encerradas nao ocupam agenda.
        if (encerrada(ordem)) {
            remover(ordem);
            return null;
        }

        if (ordem.getDataServico() == null) {
            log.debug("OS {} sem dataServico: nada a agendar.", ordem.getIdordemServico());
            return ordem.getGoogleEventId();
        }

        try {
            Map<String, Object> evento = montarEvento(ordem);

            if (ordem.getGoogleEventId() == null || ordem.getGoogleEventId().isBlank()) {
                String eventoId = calendarClient.criarEvento(evento, notificarConvidados);
                log.info("OS {} agendada no Google Calendar (evento {}).", ordem.getIdordemServico(), eventoId);
                return eventoId;
            }

            calendarClient.atualizarEvento(ordem.getGoogleEventId(), evento, notificarConvidados);
            log.info("Evento {} atualizado a partir da OS {}.",
                    ordem.getGoogleEventId(), ordem.getIdordemServico());
            return ordem.getGoogleEventId();

        } catch (GoogleCalendarException ex) {
            log.error("Falha ao sincronizar a OS {} com o Google Calendar. A ordem foi salva normalmente.",
                    ordem.getIdordemServico(), ex);
            return ordem.getGoogleEventId();
        }
    }

    /** Remove o evento da agenda, se existir. */
    public void remover(Ordemservico ordem) {
        if (!integracaoAtiva() || ordem == null
                || ordem.getGoogleEventId() == null || ordem.getGoogleEventId().isBlank()) {
            return;
        }
        try {
            calendarClient.removerEvento(ordem.getGoogleEventId(), notificarConvidados);
            log.info("Evento {} removido da agenda (OS {}).",
                    ordem.getGoogleEventId(), ordem.getIdordemServico());
        } catch (GoogleCalendarException ex) {
            log.error("Falha ao remover o evento da OS {} da agenda.", ordem.getIdordemServico(), ex);
        }
    }

    // ===== Montagem do evento =====

    private Map<String, Object> montarEvento(Ordemservico ordem) {
        LocalDateTime inicio = inicioDoAtendimento(ordem);
        LocalDateTime fim = inicio.plusMinutes(Math.max(15, duracaoPadraoMinutos));

        Map<String, Object> evento = new LinkedHashMap<>();
        evento.put("summary", titulo(ordem));
        evento.put("description", descricao(ordem));
        evento.put("location", localizacao(ordem.getEndereco()));
        evento.put("start", instante(inicio));
        evento.put("end", instante(fim));

        List<Map<String, String>> convidados = convidados(ordem);
        if (!convidados.isEmpty()) {
            evento.put("attendees", convidados);
        }

        evento.put("reminders", Map.of(
                "useDefault", false,
                "overrides", List.of(Map.of("method", "popup", "minutes", Math.max(0, lembreteMinutos)))
        ));

        return evento;
    }

    private String titulo(Ordemservico ordem) {
        Cliente cliente = ordem.getCliente();
        String nome = cliente != null && cliente.getNome() != null ? cliente.getNome() : "Cliente nao informado";
        return "OS " + ordem.getIdordemServico() + " - " + nome;
    }

    private String descricao(Ordemservico ordem) {
        StringBuilder texto = new StringBuilder();
        texto.append("Ordem de servico: ").append(ordem.getIdordemServico()).append('\n');

        if (ordem.getSituacao() != null && !ordem.getSituacao().isBlank()) {
            texto.append("Situacao: ").append(ordem.getSituacao()).append('\n');
        }

        List<Relservico> servicos = relServicoRepository.findByOrdemservico(ordem.getIdordemServico());
        if (servicos != null && !servicos.isEmpty()) {
            texto.append("\nServicos:\n");
            servicos.stream()
                    .map(this::descreverServico)
                    .filter(linha -> !linha.isBlank())
                    .forEach(linha -> texto.append("- ").append(linha).append('\n'));
        }

        List<String> tecnicos = tecnicos(ordem).stream()
                .map(Funcionario::getNome)
                .filter(nome -> nome != null && !nome.isBlank())
                .toList();
        if (!tecnicos.isEmpty()) {
            texto.append("\nTecnicos: ").append(String.join(", ", tecnicos)).append('\n');
        }

        if (ordem.getObservacao() != null && !ordem.getObservacao().isBlank()) {
            texto.append("\nObservacao:\n").append(ordem.getObservacao()).append('\n');
        }

        return texto.toString();
    }

    private String descreverServico(Relservico relacao) {
        if (relacao == null) {
            return "";
        }
        if (relacao.getDescricao() != null && !relacao.getDescricao().isBlank()) {
            return relacao.getDescricao();
        }
        return relacao.getServico() != null && relacao.getServico().getDescricao() != null
                ? relacao.getServico().getDescricao()
                : "";
    }

    private String localizacao(Endereco endereco) {
        if (endereco == null) {
            return "";
        }
        List<String> partes = new ArrayList<>();
        adicionarSePreenchido(partes, juntar(endereco.getTipoLogradouro(), endereco.getLogradouro()));
        adicionarSePreenchido(partes, endereco.getNumero());
        adicionarSePreenchido(partes, endereco.getComplemento());
        adicionarSePreenchido(partes, endereco.getBairro());
        adicionarSePreenchido(partes, juntar(endereco.getCidade(), endereco.getEstado()));
        adicionarSePreenchido(partes, endereco.getCep());
        return String.join(", ", partes);
    }

    /**
     * Convidados do evento. O endereco vem do proprio campo {@code email} da
     * ordem de servico; aceita varios separados por virgula ou ponto e virgula.
     */
    private List<Map<String, String>> convidados(Ordemservico ordem) {
        if (ordem.getEmail() == null || ordem.getEmail().isBlank()) {
            return List.of();
        }
        return java.util.Arrays.stream(ordem.getEmail().split("[,;]"))
                .map(String::trim)
                .filter(email -> email.contains("@"))
                .distinct()
                .map(email -> Map.<String, String>of("email", email))
                .collect(Collectors.toList());
    }

    private List<Funcionario> tecnicos(Ordemservico ordem) {
        List<OsFuncionario> vinculos = osFuncionarioRepository.findByOrdemServico(ordem.getIdordemServico());
        if (vinculos == null) {
            return List.of();
        }
        return vinculos.stream()
                .map(OsFuncionario::getFuncionario)
                .filter(funcionario -> funcionario != null)
                .toList();
    }

    /**
     * Combina a data do servico com a hora textual da OS. Como {@code horaServico}
     * e um campo livre no banco legado, formatos invalidos caem na hora padrao em
     * vez de derrubar a criacao do evento.
     */
    private LocalDateTime inicioDoAtendimento(Ordemservico ordem) {
        LocalDate data = paraLocalDate(ordem.getDataServico());
        LocalTime hora = paraLocalTime(ordem.getHoraServico());
        return LocalDateTime.of(data, hora);
    }

    private LocalDate paraLocalDate(Date data) {
        return Instant.ofEpochMilli(data.getTime()).atZone(FUSO).toLocalDate();
    }

    private LocalTime paraLocalTime(String hora) {
        if (hora == null || hora.isBlank()) {
            return HORA_PADRAO;
        }
        String limpo = hora.trim().replace('h', ':').replace('H', ':');
        try {
            String[] partes = limpo.split(":");
            int horas = Integer.parseInt(partes[0].trim());
            int minutos = partes.length > 1 && !partes[1].isBlank() ? Integer.parseInt(partes[1].trim()) : 0;
            return LocalTime.of(Math.floorMod(horas, 24), Math.floorMod(minutos, 60));
        } catch (RuntimeException ex) {
            log.debug("horaServico '{}' nao reconhecida; usando {}.", hora, HORA_PADRAO);
            return HORA_PADRAO;
        }
    }

    private Map<String, String> instante(LocalDateTime momento) {
        return Map.of(
                "dateTime", momento.format(RFC3339_LOCAL),
                "timeZone", FUSO.getId()
        );
    }

    private boolean encerrada(Ordemservico ordem) {
        String situacao = ordem.getSituacao();
        return "Cancelada".equalsIgnoreCase(situacao) || "Finalizada".equalsIgnoreCase(situacao);
    }

    private boolean integracaoAtiva() {
        return propriedades.isConfigurado() && propriedades.temRefreshToken();
    }

    private void adicionarSePreenchido(List<String> destino, String valor) {
        if (valor != null && !valor.isBlank()) {
            destino.add(valor.trim());
        }
    }

    private String juntar(String primeiro, String segundo) {
        boolean temPrimeiro = primeiro != null && !primeiro.isBlank();
        boolean temSegundo = segundo != null && !segundo.isBlank();
        if (temPrimeiro && temSegundo) {
            return primeiro.trim() + " " + segundo.trim();
        }
        return temPrimeiro ? primeiro.trim() : (temSegundo ? segundo.trim() : "");
    }
}
