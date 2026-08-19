package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.OrdemServicoDTO;
import br.com.deskinstaller.dto.OsDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import br.com.deskinstaller.service.OrdemservicoService;
import br.com.deskinstaller.service.OsPDFService;
import br.com.deskinstaller.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import java.util.List;
@RestController
@RequestMapping({"/api/ordens-servico", "/api/ordemservico"})
@RequiredArgsConstructor
@Slf4j
public class OrdemServicoController {

    private final OrdemservicoService ordemservicoService;
    private final OsPDFService osPDFService;
    private final PdfGeneratorService pdfGeneratorService;

    @GetMapping
    public ResponseEntity<List<OrdemServicoDTO>> listarTodos() {
        return ResponseEntity.ok(ordemservicoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoDTO> buscarPorId(@PathVariable Integer id) {
        return ordemservicoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de serviço não encontrada com ID: " + id));
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<OrdemServicoDTO>> listarSituacaoNaoCanceladaOuFinalizadaUltimos7Dias() {
        return ResponseEntity.ok(ordemservicoService.listarSituacaoNaoCanceladaOuFinalizadaUltimos7Dias());
    }

    @PostMapping
    public ResponseEntity<OrdemServicoDTO> salvar(@Valid @RequestBody OrdemServicoDTO dto) {
        OrdemServicoDTO salvo = ordemservicoService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/salvar")
    public ResponseEntity<OrdemServicoDTO> salvarLegado(@Valid @RequestBody OrdemServicoDTO dto) {
        return salvar(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdemServicoDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody OrdemServicoDTO dto) {
        dto.setIdordemServico(id);
        return ResponseEntity.ok(ordemservicoService.salvar(dto));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<OrdemServicoDTO> finalizar(@PathVariable Integer id) {
        return ResponseEntity.ok(ordemservicoService.finalizar(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<OrdemServicoDTO> cancelar(@PathVariable Integer id) {
        return ResponseEntity.ok(ordemservicoService.cancelar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        ordemservicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gera e faz download do PDF da OS
     * URL: GET /api/ordemservico/{id}/pdf
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> gerarPDF(@PathVariable Integer id) {
        log.info("Gerando PDF da OS - ID: {}", id);
        OsDTO osDTO = osPDFService.buscarOsParaVisualizacao(id);

        Context context = new Context();
        context.setVariable("os", osDTO);

        byte[] pdfBytes = pdfGeneratorService.gerarPdfDeTemplate("OsHTML", context);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("OS-" + id + ".pdf")
                        .build()
        );
        headers.setContentLength(pdfBytes.length);

        log.info("PDF gerado com sucesso para OS {}", id);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
