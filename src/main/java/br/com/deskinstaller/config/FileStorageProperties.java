package br.com.deskinstaller.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuracao do armazenamento de arquivos.
 *
 * <p>Como no Google Calendar, a ausencia de credencial nao derruba a aplicacao:
 * {@link #isS3Configurado()} retorna false e o servico cai para o disco local
 * quando {@code localFallback} estiver ligado.
 */
@Component
@ConfigurationProperties(prefix = "file.storage")
@Getter
@Setter
public class FileStorageProperties {

    /** s3 ou local. */
    private String provider = "local";

    /** Grava no disco quando o S3 falha ou nao esta configurado. */
    private boolean localFallback = true;

    /** Diretorio usado pelo provider local. */
    private String localDir = "./storage";

    /** Limite por arquivo, em bytes. */
    private long maxFileSizeBytes = 10L * 1024 * 1024;

    public boolean isS3Preferido() {
        return "s3".equalsIgnoreCase(provider);
    }
}
