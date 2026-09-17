package br.com.deskinstaller.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.deskinstaller.service.OrcamentoPdfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Previa em HTML do orcamento — util para ajustar o layout sem gerar PDF a cada teste.
 * Usa exatamente o mesmo template e as mesmas variaveis do PDF.
 *
 * URL: GET /orcamento/{id}/visualizar
 *
 * @author Julio Izidoro
 */
@Controller
@RequestMapping("/orcamento")
@RequiredArgsConstructor
@Slf4j
public class OrcamentoPDFController {

    private final OrcamentoPdfService orcamentoPdfService;

    @GetMapping("/{id}/visualizar")
    public String visualizar(@PathVariable Integer id, Model model) {
        log.info("Visualizando orçamento em HTML - ID: {}", id);
        model.addAllAttributes(orcamentoPdfService.montarModelo(id));
        return "OrcamentoHTML";
    }
}
