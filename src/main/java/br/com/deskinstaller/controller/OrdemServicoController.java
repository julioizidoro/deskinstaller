package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.OrdemServicoDTO;
import br.com.deskinstaller.dto.OsDTO;
import br.com.deskinstaller.service.OrdemservicoService;
import br.com.deskinstaller.service.OsPDFService;
import br.com.deskinstaller.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ordemservico")
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
        Optional<OrdemServicoDTO> dto = ordemservicoService.buscarPorId(id);
        return dto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<OrdemServicoDTO>> listarSituacaoNaoCanceladaOuFinalizadaUltimos7Dias() {
        return ResponseEntity.ok(ordemservicoService.listarSituacaoNaoCanceladaOuFinalizadaUltimos7Dias());
    }

    @PostMapping("/salvar")
    public ResponseEntity<OrdemServicoDTO> salvar(@RequestBody OrdemServicoDTO dto) {
        OrdemServicoDTO salvo = ordemservicoService.salvar(dto);
        return ResponseEntity.ok(salvo);
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

        try {
            // 1. Buscar dados da OS
            OsDTO osDTO = osPDFService.buscarOsParaVisualizacao(id);

            // 2. Preparar contexto para o template
            Context context = new Context();
            context.setVariable("os", osDTO);

            // 3. Gerar PDF
            byte[] pdfBytes = pdfGeneratorService.gerarPdfDeTemplate("OsHTML", context);

            // 4. Preparar headers para download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename("OS-" + id + ".pdf")
                            .build()
            );
            headers.setContentLength(pdfBytes.length);

            log.info("PDF gerado com sucesso para OS {}, tamanho: {} bytes", id, pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (ResponseStatusException ex) {
            // Já é uma exceção com status HTTP — repassa diretamente
            log.warn("ResponseStatusException ao gerar PDF da OS {}: {}", id, ex.getReason());
            throw ex;
        } catch (RuntimeException e) {
            // Diferenciar NotFound de outros erros
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.contains("Ordem de Serviço não encontrada") || msg.toLowerCase().contains("not found") || msg.toLowerCase().contains("não encontrada")) {
                log.warn("OS não encontrada ({}) ao tentar gerar PDF: {}", id, msg);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de Serviço não encontrada: " + id, e);
            }

            log.error("Erro inesperado ao gerar PDF da OS {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar PDF da Ordem de Serviço: " + e.getMessage(), e);
        }
    }
}
