package br.com.deskinstaller.service.google;

import br.com.deskinstaller.config.GoogleCalendarProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;

/**
 * Troca o refresh token de longa duracao por um access token de curta duracao.
 *
 * <p>O access token do Google vale cerca de uma hora. Guardamos o valor em
 * memoria e so pedimos outro quando falta pouco para expirar, evitando uma
 * chamada de rede a cada evento criado.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleTokenService {

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

    /** Margem de seguranca para renovar antes da expiracao real. */
    private static final long MARGEM_SEGURANCA_SEGUNDOS = 60;

    private final GoogleCalendarProperties propriedades;

    private final RestClient restClient = RestClient.create();

    private String accessTokenEmCache;
    private Instant expiraEm = Instant.EPOCH;

    /**
     * Devolve um access token valido, renovando quando necessario.
     *
     * @throws GoogleCalendarException se a integracao nao estiver configurada
     *                                 ou se o Google recusar o refresh token
     */
    public synchronized String obterAccessToken() {
        if (accessTokenEmCache != null && Instant.now().isBefore(expiraEm)) {
            return accessTokenEmCache;
        }
        if (!propriedades.temRefreshToken()) {
            throw new GoogleCalendarException(
                    "GOOGLE_REFRESH_TOKEN nao configurado: nao e possivel autenticar no Google Calendar.");
        }

        MultiValueMap<String, String> formulario = new LinkedMultiValueMap<>();
        formulario.add("client_id", propriedades.getClientId());
        formulario.add("client_secret", propriedades.getClientSecret());
        formulario.add("refresh_token", propriedades.getRefreshToken());
        formulario.add("grant_type", "refresh_token");

        try {
            Map<String, Object> resposta = restClient.post()
                    .uri(TOKEN_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formulario)
                    .retrieve()
                    .body(Map.class);

            if (resposta == null || resposta.get("access_token") == null) {
                throw new GoogleCalendarException("Resposta do Google sem access_token.");
            }

            accessTokenEmCache = String.valueOf(resposta.get("access_token"));
            long duracaoSegundos = resposta.get("expires_in") instanceof Number numero
                    ? numero.longValue()
                    : 3600L;
            expiraEm = Instant.now().plusSeconds(Math.max(0, duracaoSegundos - MARGEM_SEGURANCA_SEGUNDOS));

            log.debug("Access token do Google renovado; valido por {}s.", duracaoSegundos);
            return accessTokenEmCache;

        } catch (GoogleCalendarException ex) {
            throw ex;
        } catch (Exception ex) {
            invalidarCache();
            throw new GoogleCalendarException("Falha ao renovar o access token do Google.", ex);
        }
    }

    /** Descarta o token em cache, forcando renovacao na proxima chamada. */
    public synchronized void invalidarCache() {
        accessTokenEmCache = null;
        expiraEm = Instant.EPOCH;
    }
}
