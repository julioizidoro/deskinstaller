package br.com.deskinstaller.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuracao de acesso ao servidor de WhatsApp.
 *
 * <p>Equivalente as variaveis {@code WHATSAPP_API_URL} e {@code WHATSAPP_API_KEY}
 * usadas na versao Node do cliente.
 */
@Component
@ConfigurationProperties(prefix = "whatsapp.api")
@Getter
@Setter
public class WhatsAppProperties {

    /** Endereco base do servidor de WhatsApp. */
    private String url = "http://localhost:3002";

    /** Chave enviada no cabecalho {@code x-api-key}. */
    private String key;

    /** Sessao usada quando a chamada nao informa um sessionId. */
    private String defaultSessionId;

    /** Tempo maximo de conexao, em milissegundos. */
    private int connectTimeoutMs = 10_000;

    /** Tempo maximo de leitura da resposta, em milissegundos. */
    private int readTimeoutMs = 30_000;

    public boolean temSessaoPadrao() {
        return defaultSessionId != null && !defaultSessionId.isBlank();
    }

    public boolean temChave() {
        return key != null && !key.isBlank();
    }
}
