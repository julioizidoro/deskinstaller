package br.com.deskinstaller.controller;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.deskinstaller.dto.OrcamentoDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.service.OrcamentoPdfService;
import br.com.deskinstaller.service.OrcamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Endpoints do Orcamento.
 *
 * @author Julio Izidoro
 */
@RestController
@RequestMapping({"/api/orcamentos", "/api/orcamento"})
@RequiredArgsConstructor
@Slf4j
public class OrcamentoController {

    private final OrcamentoService orcamentoService;
    private final OrcamentoPdfService orcamentoPdfService;

    @GetMapping
    public ResponseEntity<List<OrcamentoDTO>> listarTodos() {
        log.info("GET /api/orcamentos - listar todos");
        return ResponseEntity.ok(orcamentoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrcamentoDTO> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/orcamentos/{} - buscarPorId", id);
        return orcamentoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado com ID: " + id));
    }

    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<OrcamentoDTO>> listarPorCliente(@PathVariable("id") Integer idcliente) {
        log.info("GET /api/orcamentos/cliente/{}", idcliente);
        return ResponseEntity.ok(orcamentoService.listarPorCliente(idcliente));
    }

    @GetMapping("/endereco/{id}")
    public ResponseEntity<List<OrcamentoDTO>> listarPorEndereco(@PathVariable("id") Integer idendereco) {
        log.info("GET /api/orcamentos/endereco/{}", idendereco);
        return ResponseEntity.ok(orcamentoService.listarPorEndereco(idendereco));
    }

    @GetMapping("/situacao/{situacao}")
    public ResponseEntity<List<OrcamentoDTO>> listarPorSituacao(@PathVariable String situacao) {
        log.info("GET /api/orcamentos/situacao/{}", situacao);
        return ResponseEntity.ok(orcamentoService.listarPorSituacao(situacao));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<OrcamentoDTO>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fim) {
        log.info("GET /api/orcamentos/periodo");
        return ResponseEntity.ok(orcamentoService.listarPorPeriodo(inicio, fim));
    }

    /**
     * Gera o PDF do orcamento (template Thymeleaf OrcamentoHTML + OpenHTMLtoPDF).
     * GET /api/orcamentos/{id}/pdf -> application/pdf
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> gerarPdf(@PathVariable Integer id) {
        log.info("GET /api/orcamentos/{}/pdf", id);
        byte[] pdf = orcamentoPdfService.gerarPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline()
                .filename("orcamento_" + id + ".pdf")
                .build());
        headers.setContentLength(pdf.length);

        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @PostMapping
    public ResponseEntity<OrcamentoDTO> salvar(@Valid @RequestBody OrcamentoDTO dto) {
        log.info("POST /api/orcamentos - salvar orçamento");
        return ResponseEntity.status(HttpStatus.CREATED).body(orcamentoService.salvar(dto));
    }

    @PostMapping("/salvar")
    public ResponseEntity<OrcamentoDTO> salvarLegado(@Valid @RequestBody OrcamentoDTO dto) {
        return salvar(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrcamentoDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody OrcamentoDTO dto) {
        log.info("PUT /api/orcamentos/{}", id);
        dto.setIdorcamento(id);
        return ResponseEntity.ok(orcamentoService.salvar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        log.info("DELETE /api/orcamentos/{}", id);
        orcamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarLegado(@PathVariable Integer id) {
        return deletar(id);
    }
}
