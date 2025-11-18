package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.EnderecoDTO;
import br.com.deskinstaller.service.EnderecoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        return enderecoService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(criarErro("Endereco não encontrado")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Integer id) {
        try {
            enderecoService.deletar(id);
            return ResponseEntity.ok(criarMensagem("Endereco deletado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(criarErro(e.getMessage()));
        }
    }

    @PostMapping("/salvar")
    public ResponseEntity<?> salvar(@RequestBody EnderecoDTO enderecoDTO) {
        try {
            log.info("Recebido EnderecoDTO: {}", enderecoDTO);
            EnderecoDTO salvo = enderecoService.salvar(enderecoDTO);
            return ResponseEntity.status(enderecoDTO.getIdendereco() == null ? HttpStatus.CREATED : HttpStatus.OK)
                    .body(salvo);
        } catch (RuntimeException e) {
            log.error("Erro ao salvar Endereco", e);
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
