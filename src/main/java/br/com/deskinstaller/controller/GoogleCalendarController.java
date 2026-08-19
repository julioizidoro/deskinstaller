package br.com.deskinstaller.controller;

import br.com.deskinstaller.config.GoogleCalendarProperties;
import br.com.deskinstaller.service.google.GoogleOAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Autorizacao unica da agenda da empresa no Google Calendar.
 *
 * <p>Uso esperado, feito uma vez por ambiente:
 * <ol>
 *   <li>o administrador chama {@code GET /api/google/calendar/authorize} (exige token ADMIN)
 *       e recebe a URL de consentimento;</li>
 *   <li>abre essa URL no navegador e escolhe a conta dona da agenda;</li>
 *   <li>o Google redireciona para {@code /callback}, que devolve o refresh token;</li>
 *   <li>o refresh token e gravado na configuracao do ambiente e a aplicacao reiniciada.</li>
 * </ol>
 */
@RestController
@RequestMapping("/api/google/calendar")
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarController {

    private final GoogleOAuthService oauthService;
    private final GoogleCalendarProperties propriedades;

    /** Situacao atual da integracao, util para diagnostico. */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("configurado", propriedades.isConfigurado());
        corpo.put("temRefreshToken", propriedades.temRefreshToken());
        corpo.put("calendarId", propriedades.getCalendarId());
        corpo.put("redirectUri", propriedades.getRedirectUri());
        return ResponseEntity.ok(corpo);
    }

    /** Primeiro passo: gera a URL de consentimento. Restrito a ADMIN. */
    @GetMapping("/authorize")
    public ResponseEntity<Map<String, String>> autorizar() {
        String url = oauthService.urlDeAutorizacao();
        return ResponseEntity.ok(Map.of(
                "authorizationUrl", url,
                "instrucao", "Abra esta URL no navegador e escolha a conta dona da agenda."
        ));
    }

    /**
     * Retorno do Google apos o consentimento.
     *
     * <p>Publico por necessidade: o navegador chega aqui vindo do Google, sem o
     * cabecalho de autenticacao da API. A protecao vem do parametro
     * {@code state}, emitido pelo endpoint de autorizacao e usado uma unica vez.
     *
     * <p>Responde em HTML porque quem le e uma pessoa, na janela do navegador.
     */
    @GetMapping(value = "/callback", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> callback(@RequestParam(required = false) String code,
                                           @RequestParam(required = false) String state,
                                           @RequestParam(required = false) String error) {
        if (error != null) {
            return ResponseEntity.badRequest().body(pagina("Autorizacao negada",
                    "O Google retornou: " + escapar(error), null));
        }
        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(pagina("Requisicao invalida",
                    "Nenhum code foi recebido do Google.", null));
        }

        String refreshToken = oauthService.trocarCodePorRefreshToken(code, state);

        return ResponseEntity.ok(pagina(
                "Agenda autorizada",
                "Copie o refresh token abaixo para a configuracao do ambiente "
                        + "(GOOGLE_REFRESH_TOKEN) e reinicie a aplicacao. "
                        + "Ele nao sera exibido novamente.",
                refreshToken));
    }

    private String pagina(String titulo, String mensagem, String token) {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html lang=\"pt-BR\"><head><meta charset=\"utf-8\">")
                .append("<title>").append(escapar(titulo)).append("</title>")
                .append("<style>body{font-family:system-ui,sans-serif;max-width:44rem;margin:3rem auto;")
                .append("padding:0 1.5rem;line-height:1.6;color:#1f2933}")
                .append("code{display:block;background:#f5f7fa;border:1px solid #d9e2ec;border-radius:6px;")
                .append("padding:1rem;margin-top:1rem;word-break:break-all;font-size:.9rem}")
                .append("</style></head><body>")
                .append("<h1>").append(escapar(titulo)).append("</h1>")
                .append("<p>").append(escapar(mensagem)).append("</p>");

        if (token != null) {
            html.append("<code>").append(escapar(token)).append("</code>");
        }
        return html.append("</body></html>").toString();
    }

    /** Evita que qualquer valor devolvido pelo Google seja interpretado como HTML. */
    private String escapar(String valor) {
        return valor == null ? "" : valor
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
