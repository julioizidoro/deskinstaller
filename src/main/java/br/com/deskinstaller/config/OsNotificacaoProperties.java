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

    public String linkConfirmacao(Integer idOrdemServico) {
        return base() + "/confirmacao/" + idOrdemServico;
    }

    public String linkCancelamento(Integer idOrdemServico) {
        return base() + "/cancelamento/" + idOrdemServico;
    }

    private String base() {
        String base = frontendUrl == null ? "" : frontendUrl.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base;
    }
}
