package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.ClienteDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import br.com.deskinstaller.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para operações com Cliente
 *
 * @author Julio Izidoro
 * @since 2025-11-13
 */
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClienteController {

    private final ClienteService clienteService;

    /**
     * Lista todos os clientes
     * GET /api/clientes
     */
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarTodos() {
        log.info("GET /api/clientes - Listar todos os clientes");
        List<ClienteDTO> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(clientes);
    }

    /**
     * Busca cliente por ID
     * GET /api/clientes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/clientes/{} - Buscar cliente por ID", id);
        return clienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));
    }

    /**
     * Busca clientes por nome (parcial, case insensitive)
     * GET /api/clientes/buscar/nome?q=João
     */
    @GetMapping("/buscar/nome")
    public ResponseEntity<List<ClienteDTO>> buscarPorNome(@RequestParam String q) {
        log.info("GET /api/clientes/buscar/nome?q={}", q);
        List<ClienteDTO> clientes = clienteService.buscarPorNome(q);
        return ResponseEntity.ok(clientes);
    }

    /**
     * Busca cliente por email
     * GET /api/clientes/buscar/email?email=joao@example.com
     */
    @GetMapping("/buscar/email")
    public ResponseEntity<ClienteDTO> buscarPorEmail(@RequestParam String email) {
        log.info("GET /api/clientes/buscar/email?email={}", email);
        return clienteService.buscarPorEmail(email)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com email: " + email));
    }

    /**
     * Busca clientes por telefone
     * GET /api/clientes/buscar/telefone?telefone=11999999999
     */
    @GetMapping("/buscar/telefone")
    public ResponseEntity<List<ClienteDTO>> buscarPorTelefone(@RequestParam String telefone) {
        log.info("GET /api/clientes/buscar/telefone?telefone={}", telefone);
        List<ClienteDTO> clientes = clienteService.buscarPorTelefone(telefone);
        return ResponseEntity.ok(clientes);
    }

    /**
     * Busca clientes por período de nascimento
     * GET /api/clientes/buscar/nascimento?dataInicio=1990-01-01&dataFim=2000-12-31
     */
    @GetMapping("/buscar/nascimento")
    public ResponseEntity<List<ClienteDTO>> buscarPorPeriodoNascimento(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataFim) {
        log.info("GET /api/clientes/buscar/nascimento?dataInicio={}&dataFim={}", dataInicio, dataFim);
        List<ClienteDTO> clientes = clienteService.buscarPorPeriodoNascimento(dataInicio, dataFim);
        return ResponseEntity.ok(clientes);
    }

    /**
     * Busca com filtros múltiplos
     * GET /api/clientes/buscar?nome=João&email=joao@example.com&telefone=11999999999
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteDTO>> buscarComFiltros(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefone) {
        log.info("GET /api/clientes/buscar - Filtros: nome={}, email={}, telefone={}",
                 nome, email, telefone);
        List<ClienteDTO> clientes = clienteService.buscarComFiltros(nome, email, telefone);
        return ResponseEntity.ok(clientes);
    }

    /**
     * Cria novo cliente
     * POST /api/clientes
     */
    @PostMapping
    public ResponseEntity<ClienteDTO> criar(@Valid @RequestBody ClienteDTO clienteDTO) {
        log.info("POST /api/clientes - Criar novo cliente: {}", clienteDTO.getNome());
        ClienteDTO clienteSalvo = clienteService.salvar(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
    }

    @PostMapping("/salvar")
    public ResponseEntity<ClienteDTO> criarLegado(@Valid @RequestBody ClienteDTO clienteDTO) {
        return criar(clienteDTO);
    }

    /**
     * Atualiza cliente existente
     * PUT /api/clientes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody ClienteDTO clienteDTO) {
        log.info("PUT /api/clientes/{} - Atualizar cliente", id);
        clienteDTO.setIdcliente(id);
        ClienteDTO clienteAtualizado = clienteService.salvar(clienteDTO);
        return ResponseEntity.ok(clienteAtualizado);
    }

    /**
     * Deleta cliente
     * DELETE /api/clientes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        log.info("DELETE /api/clientes/{} - Deletar cliente", id);
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Conta total de clientes
     * GET /api/clientes/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> contarClientes() {
        log.info("GET /api/clientes/count - Contar total de clientes");
        long total = clienteService.contarClientes();
        Map<String, Object> response = new HashMap<>();
        response.put("total", total);
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica se email existe
     * GET /api/clientes/verificar/email?email=joao@example.com
     */
    @GetMapping("/verificar/email")
    public ResponseEntity<Map<String, Object>> verificarEmail(@RequestParam String email) {
        log.info("GET /api/clientes/verificar/email?email={}", email);
        boolean existe = clienteService.existePorEmail(email);
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("existe", existe);
        return ResponseEntity.ok(response);
    }

}
