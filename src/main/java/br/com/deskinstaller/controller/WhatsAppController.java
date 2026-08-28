package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.ApiResult;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.DebugLogoutResponse;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.DebugTestWebhookResponse;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.QrResponse;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.SendMediaResponse;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.SendPayload;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.SendResponse;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.SessionResponse;
import br.com.deskinstaller.exception.BusinessException;
import br.com.deskinstaller.service.whatsapp.WhatsAppApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Expoe o servidor de WhatsApp atraves da API.
 *
 * <p>O cabecalho opcional {@code x-whatsapp-key} sobrepoe a chave configurada no
 * ambiente, atendendo cenarios com mais de uma conta. Quando ausente, vale a
 * chave de {@code WHATSAPP_API_KEY}.
 *
 * <p>Os endpoints de sessao devolvem o status HTTP vindo do servidor de
 * WhatsApp, porque ali um 404 e informacao de negocio (sessao inexistente,
 * QR ainda nao gerado) e nao erro desta API.
 */
@RestController
@RequestMapping("/api/whatsapp")
@RequiredArgsConstructor
@Slf4j
public class WhatsAppController {

    private static final String HEADER_CHAVE = "x-whatsapp-key";

    /** Mesmos tipos e limite aceitos pelo servidor de WhatsApp. */
    private static final List<String> MIMES_MIDIA_ACEITOS = List.of(
            MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.APPLICATION_PDF_VALUE);

    private static final long TAMANHO_MAXIMO_MIDIA = 10L * 1024 * 1024;

    private final WhatsAppApiService whatsAppApiService;

    // ===== Mensagens =====

    @PostMapping("/send")
    public ResponseEntity<SendResponse> enviar(@Valid @RequestBody SendPayload payload,
                                               @RequestHeader(value = HEADER_CHAVE, required = false) String apiKey) {
        log.info("POST /api/whatsapp/send - telefone={}", payload.telefone());
        return ResponseEntity.ok(whatsAppApiService.enviarMensagem(payload, apiKey));
    }

    /**
     * Envia um documento (PDF ou imagem) em {@code multipart/form-data} e repassa
     * para {@code POST /api/whatsapp/send-media} do servidor de WhatsApp.
     *
     * <p>Campos: {@code file} e {@code telefone} obrigatórios; {@code caption} e
     * {@code sessionId} opcionais. O destino pode ser um telefone (o servidor
     * normaliza para 55DDDNUMERO) ou um chatId terminado em {@code @c.us} /
     * {@code @g.us}. Os limites validados aqui são os mesmos do servidor: PNG,
     * JPEG ou PDF, até 10 MB — melhor recusar antes de subir o arquivo.
     */
    @PostMapping(value = "/send-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SendMediaResponse> enviarMidia(
            @RequestParam("telefone") String telefone,
            @RequestPart("file") MultipartFile arquivo,
            @RequestParam(value = "caption", required = false) String caption,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestHeader(value = HEADER_CHAVE, required = false) String apiKey) {

        if (telefone == null || telefone.isBlank()) {
            throw new BusinessException("telefone é obrigatório");
        }
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessException("Arquivo é obrigatório");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO_MIDIA) {
            throw new BusinessException("Arquivo excede o limite de 10MB");
        }

        String nome = arquivo.getOriginalFilename() != null && !arquivo.getOriginalFilename().isBlank()
                ? arquivo.getOriginalFilename()
                : "documento";
        String mime = arquivo.getContentType() != null
                ? arquivo.getContentType().toLowerCase(Locale.ROOT).split(";")[0].trim()
                : "";

        if (!MIMES_MIDIA_ACEITOS.contains(mime)) {
            throw new BusinessException("Tipo de arquivo não suportado: "
                    + (mime.isEmpty() ? "desconhecido" : mime) + ". Aceitos: PNG, JPEG e PDF.");
        }

        log.info("POST /api/whatsapp/send-media - telefone={}, arquivo={} ({} bytes, {})",
                telefone, nome, arquivo.getSize(), mime);

        byte[] conteudo;
        try {
            conteudo = arquivo.getBytes();
        } catch (IOException ex) {
            throw new BusinessException("Não foi possível ler o arquivo enviado");
        }

        return ResponseEntity.ok(
                whatsAppApiService.enviarMidia(telefone, nome, mime, conteudo, apiKey, caption, sessionId));
    }

    // ===== Sessao =====

    @PostMapping("/session/{sessionId}/start")
    public ResponseEntity<SessionResponse> iniciarSessao(
            @PathVariable String sessionId,
            @RequestHeader(value = HEADER_CHAVE, required = false) String apiKey) {

        log.info("POST /api/whatsapp/session/{}/start", sessionId);
        return responder(whatsAppApiService.iniciarSessao(sessionId, apiKey));
    }

    @GetMapping("/session/{sessionId}/status")
    public ResponseEntity<SessionResponse> statusSessao(
            @PathVariable String sessionId,
            @RequestHeader(value = HEADER_CHAVE, required = false) String apiKey) {

        log.info("GET /api/whatsapp/session/{}/status", sessionId);
        return responder(whatsAppApiService.consultarSessao(sessionId, apiKey));
    }

    @PostMapping("/session/{sessionId}/terminate")
    public ResponseEntity<SessionResponse> encerrarSessao(
            @PathVariable String sessionId,
            @RequestHeader(value = HEADER_CHAVE, required = false) String apiKey) {

        log.info("POST /api/whatsapp/session/{}/terminate", sessionId);
        return responder(whatsAppApiService.encerrarSessao(sessionId, apiKey));
    }

    /** Devolve o QR code para parear o aparelho. */
    @GetMapping("/session/{sessionId}/qr")
    public ResponseEntity<QrResponse> qrSessao(
            @PathVariable String sessionId,
            @RequestHeader(value = HEADER_CHAVE, required = false) String apiKey) {

        log.info("GET /api/whatsapp/session/{}/qr", sessionId);
        return responder(whatsAppApiService.consultarQr(sessionId, apiKey));
    }

    // ----- Atalhos que usam WHATSAPP_DEFAULT_SESSION_ID -----

    @PostMapping("/session/start")
    public ResponseEntity<SessionResponse> iniciarSessaoPadrao(
            @RequestHeader(value = HEADER_CHAVE, required = false) String apiKey) {

        log.info("POST /api/whatsapp/session/start - sessão padrão");
        return responder(whatsAppApiService.iniciarSessao(null, apiKey));
    }

    @GetMapping("/session/status")
    public ResponseEntity<SessionResponse> statusSessaoPadrao(
            @RequestHeader(value = HEADER_CHAVE, required = false) String apiKey) {

        log.info("GET /api/whatsapp/session/status - sessão padrão");
        return responder(whatsAppApiService.consultarSessao(null, apiKey));
    }

    @PostMapping("/session/terminate")
    public ResponseEntity<SessionResponse> encerrarSessaoPadrao(
            @RequestHeader(value = HEADER_CHAVE, required = false) String apiKey) {

        log.info("POST /api/whatsapp/session/terminate - sessão padrão");
        return responder(whatsAppApiService.encerrarSessao(null, apiKey));
    }

    @GetMapping("/session/qr")
    public ResponseEntity<QrResponse> qrSessaoPadrao(
            @RequestHeader(value = HEADER_CHAVE, required = false) String apiKey) {

        log.info("GET /api/whatsapp/session/qr - sessão padrão");
        return responder(whatsAppApiService.consultarQr(null, apiKey));
    }

    // ===== Diagnostico =====

    @PostMapping("/debug/test-webhook")
    public ResponseEntity<DebugTestWebhookResponse> testarWebhook(@RequestBody(required = false) Object payload) {
        log.info("POST /api/whatsapp/debug/test-webhook");
        return ResponseEntity.ok(whatsAppApiService.testarWebhook(payload));
    }

    @PostMapping("/debug/logout")
    public ResponseEntity<DebugLogoutResponse> logout() {
        log.info("POST /api/whatsapp/debug/logout");
        return ResponseEntity.ok(whatsAppApiService.logout());
    }

    /** Repassa corpo e status recebidos do servidor de WhatsApp. */
    private <T> ResponseEntity<T> responder(ApiResult<T> resultado) {
        return ResponseEntity.status(resultado.status()).body(resultado.data());
    }
}
