package br.com.deskinstaller.service.google;

import br.com.deskinstaller.config.GoogleCalendarProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Autorizacao inicial da agenda da empresa.
 *
 * <p>Fluxo executado uma unica vez: um administrador abre a URL de consentimento,
 * escolhe a conta dona da agenda, e o Google devolve um {@code code} no callback.
 * Trocamos esse code por um refresh token de longa duracao, que passa a ser a
 * credencial permanente da integracao.
 *
 * <p>O parametro {@code state} e obrigatorio: ele amarra o callback a um pedido
 * de autorizacao legitimo, impedindo que alguem dispare o callback por conta
 * propria.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuthService {

    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String ESCOPO = "https://www.googleapis.com/auth/calendar";
    private static final Duration VALIDADE_STATE = Duration.ofMinutes(10);

    private final GoogleCalendarProperties propriedades;

    private final RestClient restClient = RestClient.create();
    private final SecureRandom random = new SecureRandom();
    private final Map<String, Instant> statesPendentes = new ConcurrentHashMap<>();

    /**
     * Monta a URL de consentimento do Google.
     *
     * <p>{@code access_type=offline} combinado com {@code prompt=consent} e o que
     * garante o refresh token: sem os dois, o Google devolve apenas um access
     * token de uma hora.
     */
    public String urlDeAutorizacao() {
        exigirConfiguracao();
        String state = gerarState();

        return UriComponentsBuilder.fromHttpUrl(AUTH_URL)
                .queryParam("client_id", propriedades.getClientId())
                .queryParam("redirect_uri", propriedades.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", ESCOPO)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .queryParam("state", state)
                .encode()
                .toUriString();
    }

    /**
     * Troca o code recebido no callback pelo refresh token definitivo.
     *
     * @return o refresh token, que deve ser gravado na configuracao do ambiente
     */
    public String trocarCodePorRefreshToken(String code, String state) {
        exigirConfiguracao();
        validarState(state);

        MultiValueMap<String, String> formulario = new LinkedMultiValueMap<>();
        formulario.add("code", code);
        formulario.add("client_id", propriedades.getClientId());
        formulario.add("client_secret", propriedades.getClientSecret());
        formulario.add("redirect_uri", propriedades.getRedirectUri());
        formulario.add("grant_type", "authorization_code");

        Map<String, Object> resposta;
        try {
            resposta = restClient.post()
                    .uri(TOKEN_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formulario)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception ex) {
            throw new GoogleCalendarException("Google recusou a troca do code por token. "
                    + "Verifique se o redirect_uri cadastrado no console e identico ao configurado aqui.", ex);
        }

        if (resposta == null || resposta.get("refresh_token") == null) {
            throw new GoogleCalendarException("O Google nao devolveu refresh_token. "
                    + "Isso costuma acontecer quando a conta ja autorizou esta aplicacao antes: "
                    + "remova o acesso em myaccount.google.com/permissions e repita o processo.");
        }

        log.info("Refresh token do Google Calendar obtido com sucesso.");
        return String.valueOf(resposta.get("refresh_token"));
    }

    private String gerarState() {
        limparStatesExpirados();
        byte[] material = new byte[32];
        random.nextBytes(material);
        String state = Base64.getUrlEncoder().withoutPadding().encodeToString(material);
        statesPendentes.put(state, Instant.now().plus(VALIDADE_STATE));
        return state;
    }

    private void validarState(String state) {
        if (state == null || state.isBlank()) {
            throw new GoogleCalendarException("Parametro state ausente no callback.");
        }
        Instant expiracao = statesPendentes.remove(state);
        if (expiracao == null || Instant.now().isAfter(expiracao)) {
            throw new GoogleCalendarException("Autorizacao invalida ou expirada. Reinicie o processo.");
        }
    }

    private void limparStatesExpirados() {
        Instant agora = Instant.now();
        statesPendentes.entrySet().removeIf(entrada -> agora.isAfter(entrada.getValue()));
    }

    private void exigirConfiguracao() {
        if (!propriedades.isConfigurado()) {
            throw new GoogleCalendarException("Integracao incompleta: defina GOOGLE_CLIENT_ID, "
                    + "GOOGLE_CLIENT_SECRET, GOOGLE_REDIRECT_URI e GOOGLE_CALENDAR_ID.");
        }
    }
}
