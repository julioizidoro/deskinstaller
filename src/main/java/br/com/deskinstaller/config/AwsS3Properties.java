package br.com.deskinstaller.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws")
@Getter
@Setter
public class AwsS3Properties {

    private String region = "us-east-1";
    private String accessKeyId;
    private String secretAccessKey;
    private S3 s3 = new S3();

    @Getter
    @Setter
    public static class S3 {
        private String bucket;
        /** Prefixo fixo aplicado a toda chave gravada por esta aplicacao. */
        private String prefix = "deskinstaller/dev";
        /** Validade da URL assinada de download, em segundos. */
        private long signedUrlExpiresIn = 300;
    }

    public boolean isConfigurado() {
        return preenchido(accessKeyId)
                && preenchido(secretAccessKey)
                && preenchido(region)
                && s3 != null && preenchido(s3.getBucket());
    }

    private boolean preenchido(String valor) {
        return valor != null && !valor.isEmpty();
    }
}
