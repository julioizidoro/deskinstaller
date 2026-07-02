package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.OsFinanceiroDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import br.com.deskinstaller.service.OsFinanceiroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/os/financeiro", "/api/osfinanceiro"})
@RequiredArgsConstructor
@Slf4j
public class OsFinanceiroController {

    private final OsFinanceiroService osFinanceiroService;

    @GetMapping("/os/{id}")
    public ResponseEntity<List<OsFinanceiroDTO>> listarOS(@PathVariable("id") Integer idOrdemServico) {
        log.info("GET /api/os/financeiro/os/{} - listar OsFinanceiro por id OS", idOrdemServico);
        return ResponseEntity.ok(osFinanceiroService.listarOS(idOrdemServico));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OsFinanceiroDTO> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/os/financeiro/{} - buscarPorId", id);
        return osFinanceiroService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("OsFinanceiro não encontrado com ID: " + id));
    }

    @PostMapping
    public ResponseEntity<OsFinanceiroDTO> salvar(@Valid @RequestBody OsFinanceiroDTO dto) {
        log.info("POST /api/os/financeiro - salvar: {}", dto);
        OsFinanceiroDTO salvo = osFinanceiroService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/salvar")
    public ResponseEntity<OsFinanceiroDTO> salvarLegado(@Valid @RequestBody OsFinanceiroDTO dto) {
        return salvar(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OsFinanceiroDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody OsFinanceiroDTO dto) {
        dto.setIdosfinanceiro(id);
        return ResponseEntity.ok(osFinanceiroService.salvar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        osFinanceiroService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarLegado(@PathVariable Integer id) {
        return deletar(id);
    }
}
