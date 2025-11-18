package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.FuncionarioDTO;
import br.com.deskinstaller.service.FuncionarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/funcionarios/{} - Buscar funcionario por ID", id);
        return funcionarioService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(criarErro("Funcionario não encontrado com ID: " + id)));
    }

    @PostMapping("/salvar")
    public ResponseEntity<?> salvar(@RequestBody FuncionarioDTO dto) {
        try {
            log.info("POST /api/funcionarios/salvar - Salvar funcionario: {}", dto);
            FuncionarioDTO salvo = funcionarioService.salvar(dto);
            HttpStatus status = (dto.getIdfuncionario() == null) ? HttpStatus.CREATED : HttpStatus.OK;
            return ResponseEntity.status(status).body(salvo);
        } catch (RuntimeException e) {
            log.error("Erro ao salvar funcionario", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(criarErro(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Integer id) {
        try {
            funcionarioService.deletar(id);
            return ResponseEntity.ok(criarMensagem("Funcionario deletado com sucesso"));
        } catch (RuntimeException e) {
            log.error("Erro ao deletar funcionario", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(criarErro(e.getMessage()));
        }
    }

    private Map<String, Object> criarErro(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("erro", msg);
        return m;
    }

    private Map<String, Object> criarMensagem(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("mensagem", msg);
        return m;
    }
}
