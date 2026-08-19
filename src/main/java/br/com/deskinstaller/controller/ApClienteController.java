package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.ApclienteDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import br.com.deskinstaller.service.ApClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aparelhos")
@RequiredArgsConstructor
@Slf4j
public class ApClienteController {

    private final ApClienteService apClienteService;

    @GetMapping
    public ResponseEntity<List<ApclienteDTO>> listar(@RequestParam(value = "clienteId", required = false) Integer clienteId) {
        log.info("GET /api/aparelhos - listar (clienteId={})", clienteId);
        if (clienteId != null) {
            return ResponseEntity.ok(apClienteService.listarPorCliente(clienteId));
        }
        return ResponseEntity.ok(apClienteService.listarTodos());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ApclienteDTO>> listarPorCliente(@PathVariable Integer clienteId) {
        log.info("GET /api/aparelhos/cliente/{} - listarPorCliente", clienteId);
        return ResponseEntity.ok(apClienteService.listarPorCliente(clienteId));
    }

    @GetMapping("/cliente/{clienteId}/endereco/{enderecoId}")
    public ResponseEntity<List<ApclienteDTO>> listarPorClienteEndereco(@PathVariable Integer clienteId, @PathVariable Integer enderecoId) {
        log.info("GET /api/aparelhos/cliente/{}/endereco/{} - listarPorClienteEndereco", clienteId, enderecoId);
        return ResponseEntity.ok(apClienteService.listarPorClienteEndereco(clienteId, enderecoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApclienteDTO> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/aparelhos/{} - buscarPorId", id);
        return apClienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Apcliente não encontrado com ID: " + id));
    }

    @PostMapping
    public ResponseEntity<ApclienteDTO> salvar(@Valid @RequestBody ApclienteDTO dto) {
        log.info("POST /api/aparelhos - salvar: {}", dto);
        ApclienteDTO salvo = apClienteService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/salvar")
    public ResponseEntity<ApclienteDTO> salvarLegado(@Valid @RequestBody ApclienteDTO dto) {
        return salvar(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApclienteDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody ApclienteDTO dto) {
        dto.setIdapCliente(id);
        return ResponseEntity.ok(apClienteService.salvar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        apClienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
