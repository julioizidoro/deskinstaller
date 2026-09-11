package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.ArquivoDTO;
import br.com.deskinstaller.service.ArquivoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Arquivos enviados pela aplicacao.
 *
 * <p>A pasta e livre, informada em cada upload, e o par refTipo/refId permite a
 * consulta direta por dono. Exemplos de uso:
 * <pre>
 *   POST /api/arquivos  (multipart)
 *        arquivo=@foto.jpg
 *        pasta=cliente/1956/aparelho/33
 *        refTipo=APARELHO&amp;refId=33
 *
 *   POST /api/arquivos  (multipart)
 *        arquivo=@laudo.pdf
 *        pasta=cliente/1956/os/5455
 *        refTipo=OS&amp;refId=5455
 *
 *   GET  /api/arquivos?refTipo=OS&amp;refId=5455
 *   GET  /api/arquivos?pasta=cliente/1956&amp;incluirSubpastas=true
 *   GET  /api/arquivos/12/download
 *   DELETE /api/arquivos/12
 * </pre>
 */
@RestController
@RequestMapping("/api/arquivos")
@RequiredArgsConstructor
@Slf4j
public class ArquivoController {

    private final ArquivoService arquivoService;

    /** Upload. A resposta ja traz a URL assinada quando o destino for o S3. */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArquivoDTO> upload(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("pasta") String pasta,
            @RequestParam(value = "refTipo", required = false) String refTipo,
            @RequestParam(value = "refId", required = false) Integer refId,
            @RequestParam(value = "descricao", required = false) String descricao) {

        log.info("POST /api/arquivos - pasta={} refTipo={} refId={}", pasta, refTipo, refId);
        return ResponseEntity.ok(arquivoService.upload(arquivo, pasta, refTipo, refId, descricao));
    }

    /**
     * Consulta por dono ({@code refTipo} + {@code refId}) ou por pasta.
     * Um dos dois criterios e obrigatorio.
     */
    @GetMapping
    public ResponseEntity<List<ArquivoDTO>> listar(
            @RequestParam(value = "refTipo", required = false) String refTipo,
            @RequestParam(value = "refId", required = false) Integer refId,
            @RequestParam(value = "pasta", required = false) String pasta,
            @RequestParam(value = "incluirSubpastas", defaultValue = "false") boolean incluirSubpastas) {

        if (refTipo != null && refId != null) {
            return ResponseEntity.ok(arquivoService.listarPorReferencia(refTipo, refId));
        }
        if (pasta != null && !pasta.isBlank()) {
            return ResponseEntity.ok(arquivoService.listarPorPasta(pasta, incluirSubpastas));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArquivoDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(arquivoService.buscarPorId(id));
    }

    /**
     * Download pela propria API. Sempre funciona, inclusive no provider local;
     * para o S3 costuma ser preferivel usar a {@code urlDownload} assinada.
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Integer id) {
        ArquivoService.ConteudoArquivo conteudo = arquivoService.baixar(id);
        String nome = URLEncoder.encode(conteudo.nomeOriginal(), StandardCharsets.UTF_8).replace("+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + nome)
                .contentType(tipoSeguro(conteudo.contentType()))
                .contentLength(conteudo.conteudo().length)
                .body(new ByteArrayResource(conteudo.conteudo()));
    }

    /** Um content-type invalido gravado no upload nao pode derrubar o download. */
    private MediaType tipoSeguro(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        try {
            return MediaType.parseMediaType(contentType);
        } catch (RuntimeException ex) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        arquivoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
