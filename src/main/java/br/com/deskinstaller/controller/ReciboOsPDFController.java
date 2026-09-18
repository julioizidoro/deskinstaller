package br.com.deskinstaller.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.deskinstaller.service.ReciboOsPdfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Previa em HTML do recibo da OS — mesmo template e mesmas variaveis do PDF,
 * util para ajustar o layout sem gerar PDF a cada teste.
 *
 * URL: GET /recibo-os/{id}/visualizar
 *
 * @author Julio Izidoro
 */
@Controller
@RequestMapping("/recibo-os")
@RequiredArgsConstructor
@Slf4j
public class ReciboOsPDFController {

    private final ReciboOsPdfService reciboOsPdfService;

    @GetMapping("/{id}/visualizar")
    public String visualizar(@PathVariable Integer id, Model model) {
        log.info("Visualizando recibo da OS em HTML - ID: {}", id);
        model.addAllAttributes(reciboOsPdfService.montarModelo(id));
        return "ReciboOsHTML";
    }
}
