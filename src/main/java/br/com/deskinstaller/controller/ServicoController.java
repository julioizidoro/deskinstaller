package br.com.deskinstaller.controller;


import br.com.deskinstaller.dto.ServicoDTO;
import br.com.deskinstaller.service.ServicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
@Slf4j
public class ServicoController {

    private final ServicoService servicoService;

    @GetMapping
    public ResponseEntity<List<ServicoDTO>> listar() {
        log.info("GET /api/servicos - listar Todos os Servicos");
        return ResponseEntity.ok(servicoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/servicos/{} - buscarPorId", id);
        return servicoService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(criarErro("Servico não encontrado com ID: " + id)));
    }

    private Map<String, Object> criarErro(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("erro", msg);
        return m;
    }
}
