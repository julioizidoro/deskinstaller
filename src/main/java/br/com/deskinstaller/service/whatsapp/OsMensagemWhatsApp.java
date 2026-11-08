package br.com.deskinstaller.service.whatsapp;

import br.com.deskinstaller.config.OsNotificacaoProperties;
import br.com.deskinstaller.dto.ConfirmacaoOsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Monta o texto de WhatsApp enviado ao cliente um dia antes do servico,
 * com os dados da OS e os links de confirmacao e cancelamento.
 */
@Component
@RequiredArgsConstructor
public class OsMensagemWhatsApp {

    private static final Locale PT_BR = new Locale("pt", "BR");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final OsNotificacaoProperties propriedades;

    // Emojis como escapes unicode para nao depender do encoding do fonte.
    private static final String E_FERRAMENTA = "\uD83D\uDD27";            // 🔧
    private static final String E_PRANCHETA = "\uD83D\uDCCB";            // 📋
    private static final String E_CALENDARIO = "\uD83D\uDCC5";           // 📅
    private static final String E_RELOGIO = "\uD83D\uDD52";              // 🕒
    private static final String E_PIN = "\uD83D\uDCCD";                  // 📍
    private static final String E_DINHEIRO = "\uD83D\uDCB0";             // 💰
    private static final String E_FERRAMENTAS = "\uD83D\uDEE0\uFE0F";    // 🛠️
    private static final String E_NOTA = "\uD83D\uDCDD";                 // 📝
    private static final String E_OK = "\u2705";                          // ✅
    private static final String E_X = "\u274C";                           // ❌
    private static final String E_ALERTA = "\u26A0\uFE0F";                // ⚠️
    private static final String E_FLOCO = "\u2744\uFE0F";                 // ❄️

    public String montar(ConfirmacaoOsDTO os) {
        String cliente = vazio(os.getClienteNome()) ? "cliente" : os.getClienteNome().trim();

        String dataTexto = "";
        String diaSemana = "";
        if (os.getDataServico() != null) {
            // getTime() evita UnsupportedOperationException de java.sql.Date.toInstant()
            LocalDate dia = Instant.ofEpochMilli(os.getDataServico().getTime())
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            dataTexto = DATA.format(dia);
            diaSemana = dia.getDayOfWeek().getDisplayName(TextStyle.FULL, PT_BR);
        }

        List<String> l = new ArrayList<>();
        l.add("Olá, " + cliente + "!");
        l.add("");
        l.add(E_FERRAMENTA + " *ONDA TÉRMICA | Confirmação de Agendamento*");
        l.add("");
        l.add("Seu atendimento está agendado! Confira abaixo os detalhes do serviço:");
        l.add("");
        l.add(E_PRANCHETA + " *Ordem de Serviço:* Nº " + os.getIdordemServico());
        if (!dataTexto.isEmpty()) {
            l.add(E_CALENDARIO + " *Data:* " + diaSemana + ", " + dataTexto);
        }
        if (!vazio(os.getHoraServico())) {
            l.add(E_RELOGIO + " *Horário:* " + os.getHoraServico().trim());
        }
        String endereco = endereco(os);
        if (!endereco.isEmpty()) {
            l.add(E_PIN + " *Endereço:* " + endereco);
        }
        if (os.getValor() != null) {
            l.add(E_DINHEIRO + " *Valor previsto:* " + moeda(os.getValor()));
        }

        List<String> servicos = new ArrayList<>();
        if (os.getServicos() != null) {
            for (ConfirmacaoOsDTO.ServicoItem item : os.getServicos()) {
                if (!vazio(item.getDescricao())) {
                    servicos.add(item.getDescricao().trim());
                }
            }
        }
        if (!servicos.isEmpty()) {
            l.add("");
            l.add(E_FERRAMENTAS + " *Serviço(s) agendado(s):*");
            l.addAll(servicos);
        }

        if (!vazio(os.getObservacao())) {
            l.add("");
            l.add(E_NOTA + " *Observações:*");
            l.add(os.getObservacao().trim());
        }

        l.add("");
        l.add(E_OK + " *CONFIRME SEU AGENDAMENTO*");
        l.add("");
        l.add("Para garantir seu atendimento, escolha uma das opções abaixo:");
        l.add("");
        l.add(E_OK + " *Confirmar agendamento:*");
        l.add(propriedades.linkApiConfirmar(os.getIdordemServico()));
        l.add("");
        l.add(E_X + " *Cancelar agendamento:*");
        l.add(propriedades.linkApiCancelar(os.getIdordemServico()));
        l.add("");
        l.add(E_ALERTA + " *Importante:* Caso precise alterar a data ou o horário, entre em contato conosco com antecedência.");
        l.add("");
        l.add("Agradecemos pela confiança!");
        l.add("");
        l.add("*Onda Térmica*");
        l.add(E_FLOCO + " Climatização e Serviços");

        return String.join("\n", l);
    }

    private String endereco(ConfirmacaoOsDTO os) {
        StringBuilder sb = new StringBuilder();
        juntar(sb, ", ", os.getLogradouro());
        juntar(sb, ", ", os.getNumero());
        juntar(sb, ", ", os.getComplemento());
        juntar(sb, " - ", os.getBairro());
        if (!vazio(os.getCidade())) {
            juntar(sb, " - ", vazio(os.getEstado()) ? os.getCidade() : os.getCidade().trim() + "/" + os.getEstado().trim());
        }
        juntar(sb, " - CEP ", os.getCep());
        return sb.toString();
    }

    private void juntar(StringBuilder sb, String separador, String valor) {
        if (vazio(valor)) {
            return;
        }
        if (sb.length() > 0) {
            sb.append(separador);
        }
        sb.append(valor.trim());
    }

    private String moeda(double valor) {
        return NumberFormat.getCurrencyInstance(PT_BR).format(valor).replace('\u00A0', ' ');
    }

    private boolean vazio(String s) {
        return s == null || s.isBlank();
    }
}
