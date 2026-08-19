package br.com.deskinstaller.service.google;

import br.com.deskinstaller.config.GoogleCalendarProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Acesso de baixo nivel a API REST do Google Calendar.
 *
 * <p>Optamos por chamadas HTTP diretas em vez das bibliotecas oficiais do
 * Google para nao adicionar dependencias pesadas ao projeto: sao apenas tres
 * operacoes (criar, atualizar e remover evento).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarClient {

    private static final String BASE_URL = "https://www.googleapis.com/calendar/v3";

    private final GoogleCalendarProperties propriedades;
    private final GoogleTokenService tokenService;

    private final RestClient restClient = RestClient.create();

    /**
     * Cria o evento e devolve o id atribuido pelo Google.
     *
     * @param evento corpo do evento no formato da API do Calendar
     * @return id do evento criado
     */
    public String criarEvento(Map<String, Object> evento, boolean notificarConvidados) {
        String uri = UriComponentsBuilder.fromHttpUrl(urlEventos())
                .queryParam("sendUpdates", notificarConvidados ? "all" : "none")
                .toUriString();

        Map<String, Object> resposta = executar(() -> restClient.post()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.obterAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(evento)
                .retrieve()
                .body(Map.class));

        if (resposta == null || resposta.get("id") == null) {
            throw new GoogleCalendarException("Google nao retornou o id do evento criado.");
        }
        return String.valueOf(resposta.get("id"));
    }

    /** Atualiza um evento existente, substituindo apenas os campos enviados. */
    public void atualizarEvento(String eventoId, Map<String, Object> evento, boolean notificarConvidados) {
        String uri = UriComponentsBuilder.fromHttpUrl(urlEventos() + "/" + codificar(eventoId))
                .queryParam("sendUpdates", notificarConvidados ? "all" : "none")
                .toUriString();

        executar(() -> restClient.patch()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.obterAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(evento)
                .retrieve()
                .body(Map.class));
    }

    /** Remove o evento da agenda. Ignora silenciosamente se ele ja nao existir. */
    public void removerEvento(String eventoId, boolean notificarConvidados) {
        String uri = UriComponentsBuilder.fromHttpUrl(urlEventos() + "/" + codificar(eventoId))
                .queryParam("sendUpdates", notificarConvidados ? "all" : "none")
                .toUriString();

        try {
            restClient.delete()
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.obterAccessToken())
                    .retrieve()
                    .toBodilessEntity();
        } catch (GoogleCalendarException ex) {
            throw ex;
        } catch (Exception ex) {
            // 404/410 significam que o evento ja foi removido: nao e erro.
            String mensagem = String.valueOf(ex.getMessage());
            if (mensagem.contains("404") || mensagem.contains("410")) {
                log.debug("Evento {} ja nao existia na agenda.", eventoId);
                return;
            }
            throw new GoogleCalendarException("Falha ao remover o evento " + eventoId + " da agenda.", ex);
        }
    }

    private Map<String, Object> executar(Chamada chamada) {
        try {
            return chamada.executar();
        } catch (GoogleCalendarException ex) {
            throw ex;
        } catch (Exception ex) {
            // Token pode ter sido revogado do outro lado; forca renovacao na proxima tentativa.
            tokenService.invalidarCache();
            throw new GoogleCalendarException("Falha na chamada ao Google Calendar.", ex);
        }
    }

    private String urlEventos() {
        return BASE_URL + "/calendars/" + codificar(propriedades.getCalendarId()) + "/events";
    }

    private String codificar(String valor) {
        return URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }

    @FunctionalInterface
    private interface Chamada {
        Map<String, Object> executar() throws Exception;
    }
}
