package br.com.deskinstaller.service.whatsapp;

import br.com.deskinstaller.config.WhatsAppProperties;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.ApiResult;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.DebugLogoutResponse;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.DebugTestWebhookResponse;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.QrResponse;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.SendMediaResponse;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.SendPayload;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.SendResponse;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.SessionResponse;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.WebhookPayload;
import br.com.deskinstaller.dto.whatsapp.WhatsAppDTOs.WebhookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.function.Supplier;

/**
 * Cliente do servidor de WhatsApp que roda em {@code localhost:3002}.
 *
 * <p>Porte da classe {@code WhatasappApiService} escrita em TypeScript. As
 * escolhas de projeto seguem o original:
 *
 * <ul>
 *   <li>endpoints de <b>sessao</b> devolvem {@link ApiResult} com o status HTTP
 *       preservado, porque ali um 4xx e resposta de negocio (sessao inexistente,
 *       QR ainda nao gerado) e nao falha de transporte;</li>
 *   <li>os demais endpoints lancam {@link WhatsAppApiException} em caso de erro,
 *       ja com a mensagem devolvida pelo servidor quando disponivel;</li>
 *   <li>a chave de API pode vir por parametro, sobrepondo a configuracao, para
 *       atender cenarios multi-conta.</li>
 * </ul>
 *
 * <p>Usa o {@code RestClient} nativo do Spring: nenhuma dependencia nova.
 */
@Service
@Slf4j
public class WhatsAppApiService {

    private static final String HEADER_API_KEY = "x-api-key";

    private final WhatsAppProperties propriedades;
    private final RestClient http;

    public WhatsAppApiService(WhatsAppProperties propriedades) {
        this.propriedades = propriedades;

        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(propriedades.getConnectTimeoutMs());
        fabrica.setReadTimeout(propriedades.getReadTimeoutMs());

        this.http = RestClient.builder()
                .baseUrl(propriedades.getUrl() == null ? "" : propriedades.getUrl().trim())
                .requestFactory(fabrica)
                .build();
    }

    // ===== Mensagens =====

    public SendResponse enviarMensagem(SendPayload payload) {
        return enviarMensagem(payload, null);
    }

    public SendResponse enviarMensagem(SendPayload payload, String apiKey) {
        return executar(
                () -> http.post()
                        .uri("/api/whatsapp/send")
                        .header(HEADER_API_KEY, resolverApiKey(apiKey))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(payload)
                        .retrieve()
                        .body(SendResponse.class),
                "Falha ao enviar mensagem WhatsApp",
                payload);
    }

    public SendMediaResponse enviarMidia(String telefone, String fileName, String fileMime, byte[] conteudo) {
        return enviarMidia(telefone, fileName, fileMime, conteudo, null);
    }

    public SendMediaResponse enviarMidia(String telefone, String fileName, String fileMime,
                                         byte[] conteudo, String apiKey) {
        return enviarMidia(telefone, fileName, fileMime, conteudo, apiKey, null, null);
    }

    /**
     * Envia um PDF ou imagem para um contato ou grupo.
     *
     * <p>O servidor aceita {@code telefone} (normalizado para 55DDDNUMERO do lado
     * de la) ou um {@code chatId} terminado em {@code @c.us}/{@code @g.us}; os
     * dois chegam neste mesmo parametro. {@code caption} e {@code sessionId} sao
     * enviados quando informados — o servidor atual os ignora, mas versoes que os
     * usam recebem sem mudanca aqui.
     */
    public SendMediaResponse enviarMidia(String telefone, String fileName, String fileMime,
                                         byte[] conteudo, String apiKey,
                                         String caption, String sessionId) {

        ByteArrayResource arquivo = new ByteArrayResource(conteudo) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };

        HttpHeaders cabecalhosDaParte = new HttpHeaders();
        cabecalhosDaParte.setContentType(MediaType.parseMediaType(fileMime));

        MultiValueMap<String, Object> formulario = new LinkedMultiValueMap<>();
        formulario.add("telefone", telefone);
        formulario.add("file", new HttpEntity<>(arquivo, cabecalhosDaParte));
        if (caption != null && !caption.isBlank()) {
            formulario.add("caption", caption);
        }
        if (sessionId != null && !sessionId.isBlank()) {
            formulario.add("sessionId", sessionId);
        }

        return executar(
                () -> http.post()
                        .uri("/api/whatsapp/send-media")
                        .header(HEADER_API_KEY, resolverApiKey(apiKey))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(formulario)
                        .retrieve()
                        .body(SendMediaResponse.class),
                "Falha ao enviar mídia WhatsApp",
                "telefone=" + telefone + ", arquivo=" + fileName + ", mime=" + fileMime);
    }

    // ===== Sessao =====

    public ApiResult<SessionResponse> iniciarSessao(String sessionId, String apiKey) {
        String sessao = resolverSessionId(sessionId);
        return executarSemLancar(
                () -> http.post()
                        .uri("/session/start/{sessionId}", sessao)
                        .header(HEADER_API_KEY, resolverApiKey(apiKey))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{}")
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, (req, res) -> { })
                        .toEntity(SessionResponse.class),
                "Falha ao iniciar sessão WhatsApp",
                sessao);
    }

    public ApiResult<SessionResponse> consultarSessao(String sessionId, String apiKey) {
        String sessao = resolverSessionId(sessionId);
        return executarSemLancar(
                () -> http.get()
                        .uri("/session/status/{sessionId}", sessao)
                        .header(HEADER_API_KEY, resolverApiKey(apiKey))
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, (req, res) -> { })
                        .toEntity(SessionResponse.class),
                "Falha ao consultar status da sessão WhatsApp",
                sessao);
    }

    public ApiResult<SessionResponse> encerrarSessao(String sessionId, String apiKey) {
        String sessao = resolverSessionId(sessionId);
        return executarSemLancar(
                () -> http.post()
                        .uri("/session/terminate/{sessionId}", sessao)
                        .header(HEADER_API_KEY, resolverApiKey(apiKey))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{}")
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, (req, res) -> { })
                        .toEntity(SessionResponse.class),
                "Falha ao encerrar sessão WhatsApp",
                sessao);
    }

    public ApiResult<QrResponse> consultarQr(String sessionId, String apiKey) {
        String sessao = resolverSessionId(sessionId);
        return executarSemLancar(
                () -> http.get()
                        .uri("/session/qr/{sessionId}", sessao)
                        .header(HEADER_API_KEY, resolverApiKey(apiKey))
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, (req, res) -> { })
                        .toEntity(QrResponse.class),
                "Falha ao consultar QR da sessão WhatsApp",
                sessao);
    }

    // ===== Webhook e diagnostico =====

    /** O webhook nao exige chave de API, igual a versao original. */
    public WebhookResponse dispararWebhook(WebhookPayload payload) {
        return executar(
                () -> http.post()
                        .uri("/webhook/whatsapp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(payload)
                        .retrieve()
                        .body(WebhookResponse.class),
                "Falha ao disparar webhook WhatsApp",
                payload);
    }

    public DebugTestWebhookResponse testarWebhook(Object payload) {
        Object corpo = payload != null ? payload : new Object();
        return executar(
                () -> http.post()
                        .uri("/api/debug/test-webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(corpo)
                        .retrieve()
                        .body(DebugTestWebhookResponse.class),
                "Falha ao testar webhook WhatsApp",
                corpo);
    }

    public DebugLogoutResponse logout() {
        return executar(
                () -> http.post()
                        .uri("/api/debug/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{}")
                        .retrieve()
                        .body(DebugLogoutResponse.class),
                "Falha ao executar logout no WhatsApp",
                "");
    }

    // ===== Apoio =====

    /**
     * Resolve a sessao alvo: a informada pelo chamador ou, na ausencia dela,
     * {@code WHATSAPP_DEFAULT_SESSION_ID}.
     */
    public String resolverSessionId(String sessionId) {
        if (sessionId != null && !sessionId.isBlank()) {
            return sessionId.trim();
        }
        if (!propriedades.temSessaoPadrao()) {
            throw new WhatsAppApiException(
                    "WHATSAPP_DEFAULT_SESSION_ID não configurada e nenhum sessionId foi informado.");
        }
        return propriedades.getDefaultSessionId().trim();
    }

    private String resolverApiKey(String apiKey) {
        if (apiKey != null && !apiKey.isBlank()) {
            return apiKey.trim();
        }
        if (!propriedades.temChave()) {
            throw new WhatsAppApiException(
                    "WHATSAPP_API_KEY não configurada e nenhuma apiKey foi informada.");
        }
        return propriedades.getKey().trim();
    }

    /** Executa a chamada convertendo qualquer falha em {@link WhatsAppApiException}. */
    private <T> T executar(Supplier<T> chamada, String mensagem, Object contexto) {
        try {
            return chamada.get();
        } catch (WhatsAppApiException ex) {
            throw ex;
        } catch (RestClientResponseException ex) {
            String corpo = ex.getResponseBodyAsString();
            String detalhe = extrairMensagemDoServidor(corpo);
            String mensagemCompleta = detalhe != null
                    ? mensagem + ": " + detalhe
                    : mensagem + " (status: " + ex.getStatusCode().value() + ")";

            log.error("{} | status={} | contexto={} | corpo={}",
                    mensagem, ex.getStatusCode().value(), contexto, corpo);
            throw new WhatsAppApiException(mensagemCompleta, ex.getStatusCode().value(), corpo, ex);
        } catch (RuntimeException ex) {
            log.error("{} | contexto={}", mensagem, contexto, ex);
            throw new WhatsAppApiException(mensagem, ex);
        }
    }

    /** Idem, mas preservando o status HTTP em vez de lancar em respostas de erro. */
    private <T> ApiResult<T> executarSemLancar(Supplier<ResponseEntity<T>> chamada, String mensagem, Object contexto) {
        try {
            ResponseEntity<T> resposta = chamada.get();
            return new ApiResult<>(resposta.getBody(), resposta.getStatusCode().value());
        } catch (WhatsAppApiException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("{} | contexto={}", mensagem, contexto, ex);
            throw new WhatsAppApiException(mensagem, ex);
        }
    }

    /**
     * Extrai {@code erro} ou {@code message} do corpo devolvido pelo servidor,
     * sem desserializar em uma classe: o formato varia entre os endpoints.
     */
    private String extrairMensagemDoServidor(String corpo) {
        if (corpo == null || corpo.isBlank()) {
            return null;
        }
        for (String campo : new String[]{"erro", "message"}) {
            String marcador = "\"" + campo + "\"";
            int inicio = corpo.indexOf(marcador);
            if (inicio < 0) {
                continue;
            }
            int aspaInicial = corpo.indexOf('"', corpo.indexOf(':', inicio) + 1);
            int aspaFinal = aspaInicial < 0 ? -1 : corpo.indexOf('"', aspaInicial + 1);
            if (aspaInicial > 0 && aspaFinal > aspaInicial) {
                String valor = corpo.substring(aspaInicial + 1, aspaFinal);
                if (!valor.isBlank()) {
                    return valor;
                }
            }
        }
        return null;
    }
}
