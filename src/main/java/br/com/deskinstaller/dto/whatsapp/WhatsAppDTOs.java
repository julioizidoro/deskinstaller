package br.com.deskinstaller.dto.whatsapp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

/**
 * Contratos de entrada e saida do servidor de WhatsApp.
 *
 * <p>Sao records por serem apenas transporte de dados. Todos ignoram campos
 * desconhecidos: o servidor pode evoluir sem quebrar a API.
 */
public final class WhatsAppDTOs {

    private WhatsAppDTOs() {
    }

    /** Corpo de {@code POST /api/whatsapp/send}. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SendPayload(
            @NotBlank(message = "telefone é obrigatório") String telefone,
            @NotBlank(message = "mensagem é obrigatória") String mensagem
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SendResponse(Boolean sucesso) {
    }

    /** Corpo de {@code POST /webhook/whatsapp}. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record WebhookPayload(
            String telefone,
            String mensagem,
            String fileBase64,
            String fileName,
            String fileMime,
            String fileUrl
    ) {
        public static WebhookPayload texto(String telefone, String mensagem) {
            return new WebhookPayload(telefone, mensagem, null, null, null, null);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WebhookResponse(Boolean ok) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SessionResponse(
            Boolean success,
            String sessionId,
            String state,
            String pending,
            Boolean qrAvailable,
            Object status,
            String error,
            Boolean ok,
            String message
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record QrResponse(String qr, String erro) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SendMediaResponse(Boolean ok) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DebugTestWebhookResponse(Boolean sucesso, Object payload) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DebugLogoutResponse(Boolean ok, String message) {
    }

    /**
     * Resposta que preserva o status HTTP.
     *
     * <p>Usada nos endpoints de sessao, que na versao Node usam
     * {@code validateStatus: () => true}: um 4xx ali e informacao de negocio,
     * nao erro de transporte.
     */
    public record ApiResult<T>(T data, int status) {

        public boolean sucesso() {
            return status >= 200 && status < 300;
        }
    }
}
