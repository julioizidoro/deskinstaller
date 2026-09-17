package br.com.deskinstaller.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.deskinstaller.dto.FichaAtendimentoDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.service.FichaAtendimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Endpoints da Ficha de Atendimento.
 *
 * @author Julio Izidoro
 */
@RestController
@RequestMapping({"/api/fichas-atendimento", "/api/fichaatendimento"})
@RequiredArgsConstructor
@Slf4j
public class FichaAtendimentoController {

    private final FichaAtendimentoService fichaAtendimentoService;

    @GetMapping
    public ResponseEntity<List<FichaAtendimentoDTO>> listarTodos() {
        log.info("GET /api/fichas-atendimento - listar todas");
        return ResponseEntity.ok(fichaAtendimentoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FichaAtendimentoDTO> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/fichas-atendimento/{} - buscarPorId", id);
        return fichaAtendimentoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha de atendimento não encontrada com ID: " + id));
    }

    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<FichaAtendimentoDTO>> listarPorCliente(@PathVariable("id") Integer idcliente) {
        log.info("GET /api/fichas-atendimento/cliente/{}", idcliente);
        return ResponseEntity.ok(fichaAtendimentoService.listarPorCliente(idcliente));
    }

    @GetMapping("/endereco/{id}")
    public ResponseEntity<List<FichaAtendimentoDTO>> listarPorEndereco(@PathVariable("id") Integer idendereco) {
        log.info("GET /api/fichas-atendimento/endereco/{}", idendereco);
        return ResponseEntity.ok(fichaAtendimentoService.listarPorEndereco(idendereco));
    }

    @GetMapping("/funcionario/{id}")
    public ResponseEntity<List<FichaAtendimentoDTO>> listarPorFuncionario(@PathVariable("id") Integer idfuncionario) {
        log.info("GET /api/fichas-atendimento/funcionario/{}", idfuncionario);
        return ResponseEntity.ok(fichaAtendimentoService.listarPorFuncionario(idfuncionario));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<FichaAtendimentoDTO>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        log.info("GET /api/fichas-atendimento/periodo?inicio={}&fim={}", inicio, fim);
        return ResponseEntity.ok(fichaAtendimentoService.listarPorPeriodo(inicio, fim));
    }

    @PostMapping
    public ResponseEntity<FichaAtendimentoDTO> salvar(@Valid @RequestBody FichaAtendimentoDTO dto) {
        log.info("POST /api/fichas-atendimento - salvar ficha");
        FichaAtendimentoDTO salvo = fichaAtendimentoService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/salvar")
    public ResponseEntity<FichaAtendimentoDTO> salvarLegado(@Valid @RequestBody FichaAtendimentoDTO dto) {
        return salvar(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FichaAtendimentoDTO> atualizar(@PathVariable Integer id,
                                                         @Valid @RequestBody FichaAtendimentoDTO dto) {
        log.info("PUT /api/fichas-atendimento/{}", id);
        dto.setIdfichaatendimento(id);
        return ResponseEntity.ok(fichaAtendimentoService.salvar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        log.info("DELETE /api/fichas-atendimento/{}", id);
        fichaAtendimentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarLegado(@PathVariable Integer id) {
        return deletar(id);
    }
}
