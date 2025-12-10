package br.com.deskinstaller.controller;


import br.com.deskinstaller.dto.OsFinanceiroDTO;
import br.com.deskinstaller.service.OsFinanceiroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/os/financeiro")
@RequiredArgsConstructor
@Slf4j
public class OsFinanceiroController {

    private final OsFinanceiroService osFinanceiroService;

    @GetMapping("/os/{id}")
    public ResponseEntity<List<OsFinanceiroDTO>> listarOS(@PathVariable("id") Integer idOrdemServico) {
        log.info("GET /api/os/financeiro/os/{} - listar OsFinanceiro por id OS", idOrdemServico);
        return ResponseEntity.ok(osFinanceiroService.listarOS(idOrdemServico));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/os/financeiro/{} - buscarPorId", id);
        return osFinanceiroService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(criarErro("OsFinanceiro não encontrado com ID: " + id)));
    }

    @PostMapping("/salvar")
    public ResponseEntity<?> salvar(@RequestBody OsFinanceiroDTO dto) {
        try {
            log.info("POST /api/servicos/salvar - salvar: {}", dto);
            OsFinanceiroDTO salvo = osFinanceiroService.salvar(dto);
            HttpStatus status = (dto.getIdosfinanceiro() == null) ? HttpStatus.CREATED : HttpStatus.OK;
            return ResponseEntity.status(status).body(salvo);
        } catch (RuntimeException e) {
            log.error("Erro ao salvar OsFinanceiro", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(criarErro(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Integer id) {
        try {
            osFinanceiroService.deletar(id);
            return ResponseEntity.ok(criarMensagem("OsFinanceiro deletado com sucesso"));
        } catch (RuntimeException e) {
            log.error("Erro ao deletar osFinanceiro", e);
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
