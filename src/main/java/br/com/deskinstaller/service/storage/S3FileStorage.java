package br.com.deskinstaller.service.storage;

import br.com.deskinstaller.config.AwsS3Properties;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.util.Optional;

/**
 * Armazenamento no Amazon S3.
 *
 * <p>O cliente e criado sob demanda: sem credencial configurada o bean existe
 * mas responde {@code disponivel() == false}, e o servico de arquivos decide se
 * cai para o disco local.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class S3FileStorage implements FileStorage {

    private final AwsS3Properties propriedades;

    private volatile S3Client client;
    private volatile S3Presigner presigner;

    @Override
    public String tipo() {
        return "S3";
    }

    @Override
    public boolean disponivel() {
        return propriedades.isConfigurado();
    }

    @Override
    public void gravar(String chave, byte[] conteudo, String contentType) {
        exigirDisponivel();
        try {
            PutObjectRequest requisicao = PutObjectRequest.builder()
                    .bucket(propriedades.getS3().getBucket())
                    .key(chave)
                    .contentType(contentType != null ? contentType : "application/octet-stream")
                    .contentLength((long) conteudo.length)
                    .build();
            client().putObject(requisicao, RequestBody.fromBytes(conteudo));
            log.info("Arquivo gravado no S3: s3://{}/{} ({} bytes)",
                    propriedades.getS3().getBucket(), chave, conteudo.length);
        } catch (S3Exception ex) {
            throw new StorageException("Falha ao gravar o arquivo no S3: " + resumo(ex), ex);
        } catch (RuntimeException ex) {
            throw new StorageException("Falha ao gravar o arquivo no S3.", ex);
        }
    }

    @Override
    public byte[] ler(String chave) {
        exigirDisponivel();
        try {
            GetObjectRequest requisicao = GetObjectRequest.builder()
                    .bucket(propriedades.getS3().getBucket())
                    .key(chave)
                    .build();
            ResponseBytes<GetObjectResponse> resposta = client().getObjectAsBytes(requisicao);
            return resposta.asByteArray();
        } catch (NoSuchKeyException ex) {
            throw new StorageException("Arquivo nao encontrado no S3: " + chave, ex);
        } catch (RuntimeException ex) {
            throw new StorageException("Falha ao ler o arquivo do S3.", ex);
        }
    }

    @Override
    public void remover(String chave) {
        exigirDisponivel();
        try {
            client().deleteObject(DeleteObjectRequest.builder()
                    .bucket(propriedades.getS3().getBucket())
                    .key(chave)
                    .build());
            log.info("Arquivo removido do S3: s3://{}/{}", propriedades.getS3().getBucket(), chave);
        } catch (RuntimeException ex) {
            throw new StorageException("Falha ao remover o arquivo do S3.", ex);
        }
    }

    @Override
    public Optional<String> urlAssinada(String chave, String nomeParaDownload) {
        if (!disponivel()) {
            return Optional.empty();
        }
        try {
            GetObjectRequest.Builder objeto = GetObjectRequest.builder()
                    .bucket(propriedades.getS3().getBucket())
                    .key(chave);

            if (nomeParaDownload != null && !nomeParaDownload.isBlank()) {
                objeto.responseContentDisposition(
                        "attachment; filename=\"" + nomeParaDownload.replace("\"", "") + "\"");
            }

            GetObjectPresignRequest requisicao = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(propriedades.getS3().getSignedUrlExpiresIn()))
                    .getObjectRequest(objeto.build())
                    .build();

            return Optional.of(presigner().presignGetObject(requisicao).url().toString());
        } catch (RuntimeException ex) {
            log.error("Falha ao gerar URL assinada para {}", chave, ex);
            return Optional.empty();
        }
    }

    // ===== infraestrutura =====

    private void exigirDisponivel() {
        if (!disponivel()) {
            throw new StorageException("S3 nao configurado: informe AWS_ACCESS_KEY_ID, "
                    + "AWS_SECRET_ACCESS_KEY, AWS_REGION e AWS_S3_BUCKET.");
        }
    }

    private S3Client client() {
        S3Client atual = client;
        if (atual == null) {
            synchronized (this) {
                atual = client;
                if (atual == null) {
                    atual = S3Client.builder()
                            .region(Region.of(propriedades.getRegion()))
                            .credentialsProvider(credenciais())
                            .httpClient(UrlConnectionHttpClient.create())
                            .build();
                    client = atual;
                }
            }
        }
        return atual;
    }

    private S3Presigner presigner() {
        S3Presigner atual = presigner;
        if (atual == null) {
            synchronized (this) {
                atual = presigner;
                if (atual == null) {
                    atual = S3Presigner.builder()
                            .region(Region.of(propriedades.getRegion()))
                            .credentialsProvider(credenciais())
                            .build();
                    presigner = atual;
                }
            }
        }
        return atual;
    }

    private StaticCredentialsProvider credenciais() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(
                propriedades.getAccessKeyId(), propriedades.getSecretAccessKey()));
    }

    private String resumo(S3Exception ex) {
        return ex.awsErrorDetails() != null ? ex.awsErrorDetails().errorMessage() : ex.getMessage();
    }

    @PreDestroy
    void fechar() {
        if (client != null) {
            client.close();
        }
        if (presigner != null) {
            presigner.close();
        }
    }
}
