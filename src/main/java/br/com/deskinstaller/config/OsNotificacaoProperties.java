package br.com.deskinstaller.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Base do front usada nos links de agendamento da OS (confirmacao e
 * cancelamento). Propriedade para apontar o front de producao sem recompilar.
 */
@Component
@ConfigurationProperties(prefix = "os.notificacao")
@Getter
@Setter
public class OsNotificacaoProperties {

    /**
     * Base do front usada para montar os links de confirmacao e cancelamento
     * ({@code {frontendUrl}/confirmacao/{idOS}} e {@code {frontendUrl}/cancelamento/{idOS}}).
     */
    private String frontendUrl = "http://localhost:4200";

    /**
     * Base publica da API usada nos links enviados por WhatsApp
     * ({@code {apiUrl}/api/ordens-servico/{id}/confirmar} e {@code .../cancelar}).
     */
    private String apiUrl = "http://localhost:8080";

    public String linkApiConfirmar(Integer idOrdemServico) {
        return semBarraFinal(apiUrl) + "/api/ordens-servico/" + idOrdemServico + "/confirmar";
    }

    public String linkApiCancelar(Integer idOrdemServico) {
        return semBarraFinal(apiUrl) + "/api/ordens-servico/" + idOrdemServico + "/cancelar";
    }

    public String linkConfirmacao(Integer idOrdemServico) {
        return base() + "/confirmacao/" + idOrdemServico;
    }

    public String linkCancelamento(Integer idOrdemServico) {
        return base() + "/cancelamento/" + idOrdemServico;
    }

    private String base() {
        return semBarraFinal(frontendUrl);
    }

    private static String semBarraFinal(String url) {
        String base = url == null ? "" : url.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base;
    }
}
