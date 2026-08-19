package br.com.deskinstaller.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Credenciais da integracao com o Google Calendar.
 *
 * <p>Os valores vem de variaveis de ambiente ({@code GOOGLE_CLIENT_ID} e
 * companhia) e nunca do codigo. Quando a configuracao minima nao esta completa,
 * {@link #isConfigurado()} retorna false e a integracao deve ser tratada como
 * desligada, em vez de falhar em tempo de execucao.
 */
@Component
@ConfigurationProperties(prefix = "google.calendar")
@Getter
@Setter
public class GoogleCalendarProperties {

    /** OAuth 2.0 Client ID gerado no Google Cloud Console. */
    private String clientId;

    /** OAuth 2.0 Client Secret correspondente ao client id. */
    private String clientSecret;

    /** URI de retorno registrada no console, para onde o Google devolve o code. */
    private String redirectUri;

    /** Identificador da agenda alvo (o e-mail da agenda, ou "primary"). */
    private String calendarId;

    /** Refresh token de longa duracao, quando o fluxo usado emitir um. */
    private String refreshToken;

    /** Timeout, em milissegundos, das chamadas ao endpoint tokeninfo do Google. */
    private int tokeninfoTimeoutMs = 5000;

    /**
     * Configuracao minima para o fluxo OAuth funcionar. O refresh token fica de
     * fora porque so existe apos o primeiro consentimento do usuario.
     */
    public boolean isConfigurado() {
        return preenchido(clientId)
                && preenchido(clientSecret)
                && preenchido(redirectUri)
                && preenchido(calendarId);
    }

    public boolean temRefreshToken() {
        return preenchido(refreshToken);
    }

    private boolean preenchido(String valor) {
        return valor != null && !valor.isBlank();
    }
}
