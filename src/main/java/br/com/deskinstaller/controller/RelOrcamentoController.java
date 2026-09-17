package br.com.deskinstaller.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.deskinstaller.dto.RelOrcamentoDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.service.RelOrcamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Endpoints dos itens de orcamento.
 *
 * @author Julio Izidoro
 */
@RestController
@RequestMapping({"/api/rel-orcamentos", "/api/relorcamento"})
@RequiredArgsConstructor
@Slf4j
public class RelOrcamentoController {

    private final RelOrcamentoService relOrcamentoService;

    @GetMapping("/orcamento/{id}")
    public ResponseEntity<List<RelOrcamentoDTO>> listarPorOrcamento(@PathVariable("id") Integer idorcamento) {
        log.info("GET /api/rel-orcamentos/orcamento/{}", idorcamento);
        return ResponseEntity.ok(relOrcamentoService.listarPorOrcamento(idorcamento));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RelOrcamentoDTO> buscarPorId(@PathVariable Integer id) {
        return relOrcamentoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Item de orçamento não encontrado com ID: " + id));
    }

    @PostMapping
    public ResponseEntity<RelOrcamentoDTO> salvar(@Valid @RequestBody RelOrcamentoDTO dto) {
        log.info("POST /api/rel-orcamentos - salvar item");
        return ResponseEntity.status(HttpStatus.CREATED).body(relOrcamentoService.salvar(dto));
    }

    @PostMapping("/salvar")
    public ResponseEntity<RelOrcamentoDTO> salvarLegado(@Valid @RequestBody RelOrcamentoDTO dto) {
        return salvar(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RelOrcamentoDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody RelOrcamentoDTO dto) {
        dto.setIdrelorcamento(id);
        return ResponseEntity.ok(relOrcamentoService.salvar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        relOrcamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarLegado(@PathVariable Integer id) {
        return deletar(id);
    }
}
