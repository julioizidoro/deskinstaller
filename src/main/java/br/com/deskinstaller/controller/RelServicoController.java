package br.com.deskinstaller.controller;


import br.com.deskinstaller.dto.RelServicoDTO;
import br.com.deskinstaller.service.RelServicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relservicos")
@RequiredArgsConstructor
@Slf4j
public class RelServicoController {

    private final RelServicoService relServicoService;

    @GetMapping("/os/{id}")
    public ResponseEntity<List<RelServicoDTO>> listarOS(Integer idOrdemServico) {
        log.info("GET /api/RelServicos - listar Todos os Servicos por id OS");
        return ResponseEntity.ok(relServicoService.listarOS(idOrdemServico));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/RelServicos/{} - buscarPorId", id);
        return relServicoService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(criarErro("RelServico não encontrado com ID: " + id)));
    }

    @PostMapping("/salvar")
    public ResponseEntity<?> salvar(@RequestBody RelServicoDTO dto) {
        try {
            log.info("POST /api/servicos/salvar - salvar: {}", dto);
            RelServicoDTO salvo = relServicoService.salvar(dto);
            HttpStatus status = (dto.getIdrelServico() == null) ? HttpStatus.CREATED : HttpStatus.OK;
            return ResponseEntity.status(status).body(salvo);
        } catch (RuntimeException e) {
            log.error("Erro ao salvar RelServico", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(criarErro(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Integer id) {
        try {
            relServicoService.deletar(id);
            return ResponseEntity.ok(criarMensagem("RelServico deletado com sucesso"));
        } catch (RuntimeException e) {
            log.error("Erro ao deletar relServico", e);
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
