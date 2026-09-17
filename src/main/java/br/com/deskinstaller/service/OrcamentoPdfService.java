package br.com.deskinstaller.service;

import java.io.InputStream;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import br.com.deskinstaller.dto.OrcamentoDTO;
import br.com.deskinstaller.dto.RelOrcamentoDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Monta o contexto e gera o PDF do orcamento (template Thymeleaf OrcamentoHTML).
 *
 * @author Julio Izidoro
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrcamentoPdfService {

    /** Logo embutida em base64 para o OpenHTMLtoPDF nao depender de URL externa. */
    private static final String LOGO_CLASSPATH = "static/img/logo_onda_termica.png";

    private final OrcamentoService orcamentoService;
    private final PdfGeneratorService pdfGeneratorService;

    /** Variaveis do template, reaproveitadas pelo PDF e pela previa em HTML. */
    @Transactional(readOnly = true)
    public Map<String, Object> montarModelo(Integer idorcamento) {
        OrcamentoDTO orcamento = orcamentoService.buscarPorId(idorcamento)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado com ID: " + idorcamento));

        Map<String, Object> modelo = new LinkedHashMap<>();
        modelo.put("orcamento", orcamento);
        modelo.put("logoBase64", carregarLogoBase64());
        modelo.put("total", calcularTotal(orcamento));
        return modelo;
    }

    @Transactional(readOnly = true)
    public Context montarContexto(Integer idorcamento) {
        Context context = new Context();
        context.setVariables(montarModelo(idorcamento));
        return context;
    }

    @Transactional(readOnly = true)
    public byte[] gerarPdf(Integer idorcamento) {
        log.info("Gerando PDF do orçamento {}", idorcamento);
        return pdfGeneratorService.gerarPdfDeTemplate("OrcamentoHTML", montarContexto(idorcamento));
    }

    /** Soma quantidade x valor dos itens; sem itens, cai no valor gravado no orcamento. */
    private double calcularTotal(OrcamentoDTO orcamento) {
        List<RelOrcamentoDTO> itens = orcamento.getItens();
        if (itens == null || itens.isEmpty()) {
            return orcamento.getValor() != null ? orcamento.getValor() : 0d;
        }
        double total = 0d;
        for (RelOrcamentoDTO item : itens) {
            double quantidade = item.getQuantidade() != null ? item.getQuantidade() : 0d;
            total += quantidade * item.getValor();
        }
        return total;
    }

    private String carregarLogoBase64() {
        try (InputStream in = new ClassPathResource(LOGO_CLASSPATH).getInputStream()) {
            return Base64.getEncoder().encodeToString(in.readAllBytes());
        } catch (Exception e) {
            log.warn("Logo não encontrada em {} - documento seguirá sem imagem: {}", LOGO_CLASSPATH, e.getMessage());
            return null;
        }
    }
}
