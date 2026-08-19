package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.OsDTO;
import br.com.deskinstaller.service.OsPDFService;
import br.com.deskinstaller.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.context.Context;

/**
 * Controller para visualização e geração de PDF de Ordem de Serviço.
 * Usa o template Thymeleaf: OsHTML.html
 */
@Controller
@RequestMapping("/os")
@RequiredArgsConstructor
@Slf4j
public class OsPDFController {

    private final OsPDFService osPDFService;
    private final PdfGeneratorService pdfGeneratorService;

    /**
     * Visualiza a OS em HTML (para testes antes de gerar PDF)
     * URL: GET /os/{id}/visualizar
     */
    @GetMapping("/{id}/visualizar")
    public String visualizarOS(@PathVariable Integer id, Model model) {
        log.info("Visualizando OS HTML - ID: {}", id);

        try {
            // Buscar dados reais do banco via service
            OsDTO osDTO = osPDFService.buscarOsParaVisualizacao(id);
            model.addAttribute("os", osDTO);
            return "OsHTML"; // nome do template (sem .html)
        } catch (RuntimeException e) {
            log.error("Erro ao buscar OS {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Ordem de Serviço não encontrada: " + id, e);
        }
    }

    /**
     * Gera e faz download do PDF da OS
     * URL: GET /os/{id}/pdf
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

        } catch (RuntimeException e) {
            log.error("Erro ao gerar PDF da OS {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Ordem de Serviço não encontrada: " + id, e);
        }
    }
}

