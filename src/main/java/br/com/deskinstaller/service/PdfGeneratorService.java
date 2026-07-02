package br.com.deskinstaller.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import br.com.deskinstaller.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;

/**
 * Service para geração de PDF a partir de templates HTML Thymeleaf.
 * Usa OpenHTMLtoPDF para converter HTML em PDF.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGeneratorService {

    private final TemplateEngine templateEngine;

    /**
     * Gera PDF a partir de um template Thymeleaf e um contexto de dados.
     *
     * @param templateName Nome do template (sem extensão .html)
     * @param context Contexto com variáveis para o template
     * @return byte array do PDF gerado
     * @throws BusinessException se houver erro na geração do PDF
     */
    public byte[] gerarPdfDeTemplate(String templateName, Context context) {
        try {
            log.info("Gerando PDF a partir do template {}", templateName);

            // 1. Renderizar HTML usando Thymeleaf
            String html = templateEngine.process(templateName, context);

            // 2. Converter HTML para PDF
            return htmlParaPdf(html);

        } catch (Exception e) {
            log.error("Erro ao gerar PDF do template {}: {}", templateName, e.getMessage(), e);
            throw new BusinessException("Erro ao gerar PDF: " + e.getMessage());
        }
    }

    /**
     * Converte HTML (String) para PDF (byte array).
     */
    private byte[] htmlParaPdf(String html) throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            log.debug("PDF gerado com sucesso. Tamanho: {} bytes", outputStream.size());
            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("Erro ao converter HTML para PDF: {}", e.getMessage(), e);
            throw new Exception("Erro na conversão HTML -> PDF: " + e.getMessage(), e);
        }
    }
}
