package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.ApclienteDTO;
import br.com.deskinstaller.service.ApClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/aparelhos/{} - buscarPorId", id);
        return apClienteService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(criarErro("Apcliente não encontrado com ID: " + id)));
    }

    @PostMapping("/salvar")
    public ResponseEntity<?> salvar(@RequestBody ApclienteDTO dto) {
        try {
            log.info("POST /api/aparelhos/salvar - salvar: {}", dto);
            ApclienteDTO salvo = apClienteService.salvar(dto);
            HttpStatus status = (dto.getIdapCliente() == null) ? HttpStatus.CREATED : HttpStatus.OK;
            return ResponseEntity.status(status).body(salvo);
        } catch (RuntimeException e) {
            log.error("Erro ao salvar aparelho", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(criarErro(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Integer id) {
        try {
            apClienteService.deletar(id);
            return ResponseEntity.ok(criarMensagem("Apcliente deletado com sucesso"));
        } catch (RuntimeException e) {
            log.error("Erro ao deletar aparelhos", e);
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
