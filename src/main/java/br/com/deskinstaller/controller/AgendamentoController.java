package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.ConfirmacaoOsDTO;
import br.com.deskinstaller.service.ConfirmacaoOsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rotas publicas usadas pelas paginas de agendamento do front
 * ({@code /confirmacao/{id}} e {@code /cancelamento/{id}}).
 *
 * <p>Ficam fora do {@code OrdemServicoController} por um detalhe do Spring: o
 * {@code @RequestMapping} daquela classe prefixa todos os metodos com
 * {@code /api/ordens-servico}, e o caminho pedido aqui e {@code /api/confirmacao/...}.
 *
 * <p>Devolvem os dados da OS e seus servicos no formato da interface
 * {@code ConfirmacaoOs} do Angular. Nao alteram a situacao da OS: confirmar ou
 * cancelar de fato continua em {@code PATCH /api/ordens-servico/{id}/finalizar|cancelar},
 * que exige token.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AgendamentoController {

    private final ConfirmacaoOsService confirmacaoOsService;

    /** GET /api/confirmacao/{id} — dados da OS e seus serviços. */
    @GetMapping("/confirmacao/{id}")
    public ResponseEntity<ConfirmacaoOsDTO> confirmacao(@PathVariable Integer id) {
        log.info("GET /api/confirmacao/{}", id);
        return ResponseEntity.ok(confirmacaoOsService.buscarPorId(id));
    }

    /** GET /api/cancelamento/{id} — dados da OS e seus serviços. */
    @GetMapping("/cancelamento/{id}")
    public ResponseEntity<ConfirmacaoOsDTO> cancelamento(@PathVariable Integer id) {
        log.info("GET /api/cancelamento/{}", id);
        return ResponseEntity.ok(confirmacaoOsService.buscarPorId(id));
    }
}
