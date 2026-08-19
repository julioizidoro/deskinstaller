package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.EnderecoDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import br.com.deskinstaller.service.EnderecoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/enderecos")
@RequiredArgsConstructor
@Slf4j
public class EnderecoController {

    private final EnderecoService enderecoService;

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<EnderecoDTO>> listarPorCliente(@PathVariable Integer clienteId) {
        return ResponseEntity.ok(enderecoService.listarPorCliente(clienteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnderecoDTO> buscarPorId(@PathVariable Integer id) {
        return enderecoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Endereco não encontrado com ID: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        enderecoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<EnderecoDTO> salvar(@Valid @RequestBody EnderecoDTO enderecoDTO) {
        log.info("Recebido EnderecoDTO: {}", enderecoDTO);
        EnderecoDTO salvo = enderecoService.salvar(enderecoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/salvar")
    public ResponseEntity<EnderecoDTO> salvarLegado(@Valid @RequestBody EnderecoDTO enderecoDTO) {
        return salvar(enderecoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnderecoDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody EnderecoDTO enderecoDTO) {
        enderecoDTO.setIdendereco(id);
        return ResponseEntity.ok(enderecoService.salvar(enderecoDTO));
    }
}
