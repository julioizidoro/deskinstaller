package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.AgendaEmailDTO;
import br.com.deskinstaller.service.AgendaEmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API de AgendaEmail: inserir, alterar e consultar.
 *
 * <p>Nao existe endpoint de exclusao por decisao de negocio. Para tirar um
 * endereco de circulacao use {@code PATCH /{id}/desativar}.
 */
@RestController
@RequestMapping("/api/agenda-emails")
@RequiredArgsConstructor
@Slf4j
public class AgendaEmailController {

    private final AgendaEmailService agendaEmailService;

    /**
     * Lista os registros.
     *
     * @param ativo filtro opcional; ausente traz todos
     * @param q     trecho opcional do e-mail para busca parcial
     */
    @GetMapping
    public ResponseEntity<List<AgendaEmailDTO>> listar(@RequestParam(required = false) Boolean ativo,
                                                       @RequestParam(required = false) String q) {
        log.info("GET /api/agenda-emails - listar (ativo={}, q={})", ativo, q);
        if (q != null && !q.isBlank()) {
            return ResponseEntity.ok(agendaEmailService.buscarPorTrecho(q));
        }
        return ResponseEntity.ok(agendaEmailService.listarPorAtivo(ativo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaEmailDTO> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/agenda-emails/{} - buscar por id", id);
        return agendaEmailService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AgendaEmailDTO> criar(@Valid @RequestBody AgendaEmailDTO dto) {
        log.info("POST /api/agenda-emails - criar");
        return ResponseEntity.status(HttpStatus.CREATED).body(agendaEmailService.criar(dto));
    }

    /** Rota alternativa mantendo o padrao legado usado em outros recursos do projeto. */
    @PostMapping("/salvar")
    public ResponseEntity<AgendaEmailDTO> salvarLegado(@Valid @RequestBody AgendaEmailDTO dto) {
        log.info("POST /api/agenda-emails/salvar - criar (rota legada)");
        return ResponseEntity.status(HttpStatus.CREATED).body(agendaEmailService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendaEmailDTO> atualizar(@PathVariable Integer id,
                                                    @Valid @RequestBody AgendaEmailDTO dto) {
        log.info("PUT /api/agenda-emails/{} - atualizar", id);
        return ResponseEntity.ok(agendaEmailService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<AgendaEmailDTO> desativar(@PathVariable Integer id) {
        log.info("PATCH /api/agenda-emails/{}/desativar", id);
        return ResponseEntity.ok(agendaEmailService.desativar(id));
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<AgendaEmailDTO> ativar(@PathVariable Integer id) {
        log.info("PATCH /api/agenda-emails/{}/ativar", id);
        return ResponseEntity.ok(agendaEmailService.ativar(id));
    }
}
