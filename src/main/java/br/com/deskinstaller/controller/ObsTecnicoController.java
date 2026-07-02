package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.ObsTecnicoDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import br.com.deskinstaller.service.ObsTecnicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/obstecnico")
@RequiredArgsConstructor
@Slf4j
public class ObsTecnicoController {

    private final ObsTecnicoService obsTecnicoService;

    @GetMapping("/os/{id}")
    public ResponseEntity<List<ObsTecnicoDTO>> listarOS(@PathVariable("id") Integer idOrdemServico) {
        log.info("GET /api/obstecnico/os/{} - listar ObsTecnico por id OS", idOrdemServico);
        return ResponseEntity.ok(obsTecnicoService.listarOS(idOrdemServico));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObsTecnicoDTO> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/obstecnico/{} - buscarPorId", id);
        return obsTecnicoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("ObsTecnico não encontrado com ID: " + id));
    }

    @PostMapping
    public ResponseEntity<ObsTecnicoDTO> salvar(@Valid @RequestBody ObsTecnicoDTO dto) {
        log.info("POST /api/obstecnico - salvar: {}", dto);
        ObsTecnicoDTO salvo = obsTecnicoService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/salvar")
    public ResponseEntity<ObsTecnicoDTO> salvarLegado(@Valid @RequestBody ObsTecnicoDTO dto) {
        return salvar(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObsTecnicoDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody ObsTecnicoDTO dto) {
        dto.setIdobsTecnico(id);
        return ResponseEntity.ok(obsTecnicoService.salvar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        obsTecnicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
