package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.FuncionarioDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import br.com.deskinstaller.service.FuncionarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funcionarios")
@RequiredArgsConstructor
@Slf4j
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    @GetMapping
    public ResponseEntity<List<FuncionarioDTO>> listarTodos(@RequestParam(value = "ativo", required = false) Boolean ativo) {
        log.info("GET /api/funcionarios - Listar funcionarios (ativo={})", ativo);
        return ResponseEntity.ok(funcionarioService.listarPorAtivo(ativo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/funcionarios/{} - Buscar funcionario por ID", id);
        return funcionarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario não encontrado com ID: " + id));
    }

    @PostMapping
    public ResponseEntity<FuncionarioDTO> salvar(@Valid @RequestBody FuncionarioDTO dto) {
        log.info("POST /api/funcionarios - Salvar funcionario: {}", dto);
        FuncionarioDTO salvo = funcionarioService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/salvar")
    public ResponseEntity<FuncionarioDTO> salvarLegado(@Valid @RequestBody FuncionarioDTO dto) {
        return salvar(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody FuncionarioDTO dto) {
        dto.setIdfuncionario(id);
        return ResponseEntity.ok(funcionarioService.salvar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        funcionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
