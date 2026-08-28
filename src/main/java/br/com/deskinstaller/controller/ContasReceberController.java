package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.ContasReceberDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.service.ContasReceberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping({"/api/contas-receber", "/api/contasreceber"})
@RequiredArgsConstructor
@Slf4j
public class ContasReceberController {

    private final ContasReceberService contasReceberService;

    @GetMapping
    public ResponseEntity<List<ContasReceberDTO>> listarTodos() {
        return ResponseEntity.ok(contasReceberService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContasReceberDTO> buscarPorId(@PathVariable Integer id) {
        return contasReceberService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("ContasReceber não encontrado com ID: " + id));
    }

    /**
     * Títulos com vencimento no intervalo informado.
     * URL: GET /api/contas-receber/vencimento?inicio=2026-08-01&fim=2026-08-31
     */
    @GetMapping("/vencimento")
    public ResponseEntity<List<ContasReceberDTO>> listarPorVencimento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(contasReceberService.listarPorVencimento(inicio, fim));
    }

    @GetMapping("/cliente/{idcliente}")
    public ResponseEntity<List<ContasReceberDTO>> listarPorCliente(@PathVariable Integer idcliente) {
        return ResponseEntity.ok(contasReceberService.listarPorCliente(idcliente));
    }

    /**
     * Títulos em aberto: valorrecebido zerado e sem datarecebimento.
     * URL: GET /api/contas-receber/em-aberto
     */
    @GetMapping("/em-aberto")
    public ResponseEntity<List<ContasReceberDTO>> listarEmAberto() {
        return ResponseEntity.ok(contasReceberService.listarEmAberto());
    }

    @GetMapping("/usuario/{idusuario}")
    public ResponseEntity<List<ContasReceberDTO>> listarPorUsuario(@PathVariable Integer idusuario) {
        return ResponseEntity.ok(contasReceberService.listarPorUsuario(idusuario));
    }

    @PostMapping
    public ResponseEntity<ContasReceberDTO> salvar(@Valid @RequestBody ContasReceberDTO dto) {
        ContasReceberDTO salvo = contasReceberService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/salvar")
    public ResponseEntity<ContasReceberDTO> salvarLegado(@Valid @RequestBody ContasReceberDTO dto) {
        return salvar(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContasReceberDTO> atualizar(@PathVariable Integer id,
                                                     @Valid @RequestBody ContasReceberDTO dto) {
        dto.setIdcontasreceber(id);
        return ResponseEntity.ok(contasReceberService.salvar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        contasReceberService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarLegado(@PathVariable Integer id) {
        return deletar(id);
    }
}
