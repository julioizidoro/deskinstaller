package br.com.deskinstaller.service.whatsapp;

import br.com.deskinstaller.dto.ComunicacaoOsResultadoDTO;
import br.com.deskinstaller.dto.ConfirmacaoOsDTO;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.SendPayload;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.SendResponse;
import br.com.deskinstaller.service.ConfirmacaoOsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Envia por WhatsApp o aviso das OS em execução ("Aberta") agendadas para amanhã
 * e devolve o log do envio. Uma falha em uma OS não interrompe as demais.
 *
 * <p>FASE INICIAL: todo aviso vai para {@link #TELEFONE_DESTINO}, fixo no código.
 * Nenhum cliente recebe mensagem — o celular do cliente nunca é usado como destino.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ComunicacaoOsService {

    /** Destino único de todos os avisos nesta fase. Não vem de configuração nem do cliente. */
    private static final String TELEFONE_DESTINO = "554891100056";

    private final ConfirmacaoOsService confirmacaoOsService;
    private final OsMensagemWhatsApp osMensagemWhatsApp;
    private final WhatsAppApiService whatsAppApiService;

    // Sem @Transactional: a consulta é transacional no ConfirmacaoOsService e o envio
    // (chamada HTTP lenta) não deve segurar conexão com o banco.
    public ComunicacaoOsResultadoDTO comunicarAmanha() {
        LocalDate dataServico = LocalDate.now().plusDays(1);
        List<ConfirmacaoOsDTO> ordens = confirmacaoOsService.listarParaComunicar();

        List<Integer> enviadas = new ArrayList<>();
        List<ComunicacaoOsResultadoDTO.Falha> falhas = new ArrayList<>();

        for (ConfirmacaoOsDTO os : ordens) {
            Integer numero = os.getIdordemServico();
            try {
                SendResponse resposta = whatsAppApiService.enviarMensagem(
                        new SendPayload(TELEFONE_DESTINO, osMensagemWhatsApp.montar(os)));
                if (resposta != null && Boolean.TRUE.equals(resposta.sucesso())) {
                    enviadas.add(numero);
                } else {
                    falhas.add(falha(numero, "Servidor de WhatsApp não confirmou o envio"));
                }
            } catch (RuntimeException ex) {
                log.warn("Falha ao avisar OS {}: {}", numero, ex.getMessage());
                falhas.add(falha(numero, ex.getMessage()));
            }
        }

        ComunicacaoOsResultadoDTO resultado = ComunicacaoOsResultadoDTO.builder()
                .dataEnvio(LocalDateTime.now())
                .dataServico(dataServico)
                .telefoneDestino(TELEFONE_DESTINO)
                .totalOs(ordens.size())
                .totalEnviadas(enviadas.size())
                .totalFalhas(falhas.size())
                .osEnviadas(enviadas)
                .falhas(falhas)
                .build();

        log.info("Comunicar OS {}: {} encontradas, {} enviadas {} para {}, {} falhas",
                dataServico, resultado.getTotalOs(), resultado.getTotalEnviadas(), enviadas,
                TELEFONE_DESTINO, resultado.getTotalFalhas());
        return resultado;
    }

    private ComunicacaoOsResultadoDTO.Falha falha(Integer numero, String motivo) {
        return ComunicacaoOsResultadoDTO.Falha.builder().numeroOs(numero).motivo(motivo).build();
    }
}
