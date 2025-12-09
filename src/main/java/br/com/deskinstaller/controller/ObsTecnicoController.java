package br.com.deskinstaller.controller;


import br.com.deskinstaller.dto.ObsTecnicoDTO;
import br.com.deskinstaller.service.ObsTecnicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/obstecnico/{} - buscarPorId", id);
        return obsTecnicoService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(criarErro("ObsTecnico não encontrado com ID: " + id)));
    }

    @PostMapping("/salvar")
    public ResponseEntity<?> salvar(@RequestBody ObsTecnicoDTO dto) {
        try {
            log.info("POST /api/obstenico/salvar - salvar: {}", dto);
            ObsTecnicoDTO salvo = obsTecnicoService.salvar(dto);
            HttpStatus status = (dto.getIdobsTecnico() == null) ? HttpStatus.CREATED : HttpStatus.OK;
            return ResponseEntity.status(status).body(salvo);
        } catch (RuntimeException e) {
            log.error("Erro ao salvar ObsTecnico", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(criarErro(e.getMessage()));
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
