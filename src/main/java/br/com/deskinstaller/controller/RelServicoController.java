package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.RelServicoDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import br.com.deskinstaller.service.RelServicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/rel-servicos", "/api/relservico"})
@RequiredArgsConstructor
@Slf4j
public class RelServicoController {

    private final RelServicoService relServicoService;

    @GetMapping("/os/{id}")
    public ResponseEntity<List<RelServicoDTO>> listarOS(@PathVariable("id") Integer idOrdemServico) {
        log.info("GET /api/rel-servicos/os/{} - listar serviços por ordem", idOrdemServico);
        return ResponseEntity.ok(relServicoService.listarOS(idOrdemServico));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RelServicoDTO> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/rel-servicos/{} - buscarPorId", id);
        return relServicoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Relação de serviço não encontrada com ID: " + id));
    }

    @PostMapping
    public ResponseEntity<RelServicoDTO> salvar(@Valid @RequestBody RelServicoDTO dto) {
        log.info("POST /api/rel-servicos - salvar relação de serviço");
        RelServicoDTO salvo = relServicoService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/salvar")
    public ResponseEntity<RelServicoDTO> salvarLegado(@Valid @RequestBody RelServicoDTO dto) {
        return salvar(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RelServicoDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody RelServicoDTO dto) {
        dto.setIdrelServico(id);
        return ResponseEntity.ok(relServicoService.salvar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        relServicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarLegado(@PathVariable Integer id) {
        return deletar(id);
    }
}
