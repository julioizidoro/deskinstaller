package br.com.deskinstaller.service.whatsapp;

import br.com.deskinstaller.config.OsNotificacaoProperties;
import br.com.deskinstaller.model.Ordemservico;
import br.com.deskinstaller.repository.OrdemServicoRepository;
import br.com.deskinstaller.service.DomainValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Links de agendamento (confirmacao e cancelamento) da ordem de servico.
 *
 * <p>Nenhum dos dois envia mensagem nem altera a {@code situacao} da OS: os
 * dois gravam apenas a resposta do cliente em {@code statuscliente}
 * ({@code Confirmada} ou {@code Cancelar}). Finalizar ou cancelar de fato
 * continua em {@code PATCH /api/ordens-servico/{id}/finalizar|cancelar},
 * feito pelo usuario do sistema.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OsNotificacaoService {

    private final DomainValidationService domainValidationService;
    private final OrdemServicoRepository ordemServicoRepository;
    private final OsNotificacaoProperties propriedades;

    /** Status gravado quando o cliente abre o link de confirmacao. */
    private static final String STATUS_CONFIRMADA = "Confirmada";

    /** Status gravado quando o cliente abre o link de cancelamento. */
    private static final String STATUS_CANCELAR = "Cancelar";

    /**
     * Marca a OS como confirmada pelo cliente ({@code statuscliente = Confirmada})
     * e devolve {@code {frontendUrl}/confirmacao/{idOS}}. 404 se a OS nao existir.
     *
     * <p>So mexe em {@code statuscliente}: a {@code situacao} da OS continua sendo
     * decidida pelo usuario do sistema.
     */
    @Transactional
    public String linkConfirmacao(Integer idOrdemServico) {
        Ordemservico ordem = domainValidationService.requireOrdemServico(idOrdemServico);

        if (!STATUS_CONFIRMADA.equalsIgnoreCase(ordem.getStatuscliente())) {
            ordem.setStatuscliente(STATUS_CONFIRMADA);
            ordemServicoRepository.save(ordem);
            log.info("OS {} marcada como confirmada pelo cliente", ordem.getIdordemServico());
        }
        return propriedades.linkConfirmacao(ordem.getIdordemServico());
    }

    /**
     * Marca a OS como cancelamento pedido pelo cliente
     * ({@code statuscliente = Cancelar}) e devolve
     * {@code {frontendUrl}/cancelamento/{idOS}}. 404 se a OS nao existir.
     *
     * <p>So mexe em {@code statuscliente}: a OS nao e cancelada aqui — isso
     * continua em {@code PATCH /api/ordens-servico/{id}/cancelar}, feito pelo
     * usuario do sistema.
     */
    @Transactional
    public String linkCancelamento(Integer idOrdemServico) {
        Ordemservico ordem = domainValidationService.requireOrdemServico(idOrdemServico);

        if (!STATUS_CANCELAR.equalsIgnoreCase(ordem.getStatuscliente())) {
            ordem.setStatuscliente(STATUS_CANCELAR);
            ordemServicoRepository.save(ordem);
            log.info("OS {} marcada com pedido de cancelamento do cliente", ordem.getIdordemServico());
        }
        return propriedades.linkCancelamento(ordem.getIdordemServico());
    }
}
