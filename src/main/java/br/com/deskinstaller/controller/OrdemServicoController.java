package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.ConfirmacaoOsDTO;
import br.com.deskinstaller.dto.OrdemServicoDTO;
import br.com.deskinstaller.dto.OsDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import br.com.deskinstaller.service.ConfirmacaoOsService;
import br.com.deskinstaller.service.OrdemservicoService;
import br.com.deskinstaller.service.OsPDFService;
import br.com.deskinstaller.service.PdfGeneratorService;
import br.com.deskinstaller.service.whatsapp.OsNotificacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping({"/api/ordens-servico", "/api/ordemservico"})
@RequiredArgsConstructor
@Slf4j
public class OrdemServicoController {

    private final OrdemservicoService ordemservicoService;
    private final OsPDFService osPDFService;
    private final PdfGeneratorService pdfGeneratorService;
    private final OsNotificacaoService osNotificacaoService;
    private final ConfirmacaoOsService confirmacaoOsService;

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

    /**
     * Agenda do dia: ordens de serviço de uma data, em qualquer situação.
     * URL: GET /api/ordens-servico/agenda?data=2026-08-25&funcionarioId=3
     * Sem o parâmetro "data" considera o dia atual do servidor.
     */
    @GetMapping("/agenda")
    public ResponseEntity<List<OrdemServicoDTO>> listarAgendaDoDia(
            @RequestParam(value = "data", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(value = "funcionarioId", required = false) Integer funcionarioId) {
        return ResponseEntity.ok(ordemservicoService.listarPorData(data, funcionarioId));
    }

    /**
     * Ordens de serviço de uma data, cada uma já com os seus serviços — rota
     * pública, usada pelo batch de aviso de agenda no WhatsApp, que roda sem
     * sessão de usuário e não precisa de uma chamada extra por OS.
     * URL: GET /api/ordens-servico/data?data=2026-08-29
     * Sem o parâmetro "data" considera o dia atual do servidor.
     */
    @GetMapping("/data")
    public ResponseEntity<List<ConfirmacaoOsDTO>> listarPorData(
            @RequestParam(value = "data", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        log.info("GET /api/ordens-servico/data?data={}", data);
        return ResponseEntity.ok(confirmacaoOsService.listarPorData(data));
    }

    /**
     * Atalho para a agenda do dia atual.
     * URL: GET /api/ordens-servico/hoje?funcionarioId=3
     */
    @GetMapping("/hoje")
    public ResponseEntity<List<OrdemServicoDTO>> listarAgendaDeHoje(
            @RequestParam(value = "funcionarioId", required = false) Integer funcionarioId) {
        return ResponseEntity.ok(ordemservicoService.listarPorData(LocalDate.now(), funcionarioId));
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

    /**
     * Marca a OS como confirmada pelo cliente (statuscliente = "Confirmada") e
     * redireciona para {frontendUrl}/confirmacao/{id}. A situação da OS não muda.
     * URL: GET /api/ordens-servico/{id}/confirmar
     */
    @GetMapping("/{id}/confirmar")
    public ResponseEntity<Void> confirmar(@PathVariable Integer id) {
        String link = osNotificacaoService.linkConfirmacao(id);
        log.info("GET /api/ordens-servico/{}/confirmar -> {}", id, link);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(link))
                .build();
    }

    /**
     * Marca o pedido de cancelamento do cliente (statuscliente = "Cancelar") e
     * redireciona para {frontendUrl}/cancelamento/{id}. Não cancela a OS — isso
     * continua em {@code PATCH /api/ordens-servico/{id}/cancelar}.
     * URL: GET /api/ordens-servico/{id}/cancelar
     */
    @GetMapping("/{id}/cancelar")
    public ResponseEntity<Void> linkCancelamento(@PathVariable Integer id) {
        String link = osNotificacaoService.linkCancelamento(id);
        log.info("GET /api/ordens-servico/{}/cancelar -> {}", id, link);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(link))
                .build();
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
