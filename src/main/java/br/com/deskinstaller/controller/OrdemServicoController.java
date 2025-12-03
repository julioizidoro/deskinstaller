package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.OrdemServicoDTO;
import br.com.deskinstaller.service.OrdemservicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ordemservico")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemservicoService ordemservicoService;

    @GetMapping
    public ResponseEntity<List<OrdemServicoDTO>> listarTodos() {
        return ResponseEntity.ok(ordemservicoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoDTO> buscarPorId(@PathVariable Integer id) {
        Optional<OrdemServicoDTO> dto = ordemservicoService.buscarPorId(id);
        return dto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<OrdemServicoDTO>> listarSituacaoNaoCanceladaOuFinalizadaUltimos7Dias() {
        return ResponseEntity.ok(ordemservicoService.listarSituacaoNaoCanceladaOuFinalizadaUltimos7Dias());
    }

    @PostMapping("/salvar"    )
    public ResponseEntity<OrdemServicoDTO> salvar(@RequestBody OrdemServicoDTO dto) {
        OrdemServicoDTO salvo = ordemservicoService.salvar(dto);
        return ResponseEntity.ok(salvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        ordemservicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

