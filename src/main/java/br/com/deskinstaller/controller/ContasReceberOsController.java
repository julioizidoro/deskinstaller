package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.ContasReceberOsDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.service.ContasReceberOsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/contas-receber-os", "/api/contasreceberos"})
@RequiredArgsConstructor
@Slf4j
public class ContasReceberOsController {

    private final ContasReceberOsService contasReceberOsService;

    /** Vínculos de um título a receber. */
    @GetMapping("/conta/{id}")
    public ResponseEntity<List<ContasReceberOsDTO>> listarPorContasReceber(@PathVariable Integer id) {
        return ResponseEntity.ok(contasReceberOsService.listarPorContasReceber(id));
    }

    /** Vínculos de uma ordem de serviço. */
    @GetMapping("/os/{id}")
    public ResponseEntity<List<ContasReceberOsDTO>> listarPorOrdemServico(@PathVariable Integer id) {
        return ResponseEntity.ok(contasReceberOsService.listarPorOrdemServico(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContasReceberOsDTO> buscarPorId(@PathVariable Integer id) {
        return contasReceberOsService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("ContasReceberOs não encontrado com ID: " + id));
    }

    @PostMapping
    public ResponseEntity<ContasReceberOsDTO> salvar(@Valid @RequestBody ContasReceberOsDTO dto) {
        ContasReceberOsDTO salvo = contasReceberOsService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/salvar")
    public ResponseEntity<ContasReceberOsDTO> salvarLegado(@Valid @RequestBody ContasReceberOsDTO dto) {
        return salvar(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContasReceberOsDTO> atualizar(@PathVariable Integer id,
                                                       @Valid @RequestBody ContasReceberOsDTO dto) {
        dto.setIdcontasreceberos(id);
        return ResponseEntity.ok(contasReceberOsService.salvar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        contasReceberOsService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarLegado(@PathVariable Integer id) {
        return deletar(id);
    }
}
