package br.com.deskinstaller.service.storage;

import br.com.deskinstaller.config.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Armazenamento em disco. Serve como provider principal em desenvolvimento e
 * como rede de seguranca quando o S3 falha.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LocalFileStorage implements FileStorage {

    private final FileStorageProperties propriedades;

    @Override
    public String tipo() {
        return "LOCAL";
    }

    @Override
    public boolean disponivel() {
        return propriedades.getLocalDir() != null && !propriedades.getLocalDir().isBlank();
    }

    @Override
    public void gravar(String chave, byte[] conteudo, String contentType) {
        Path destino = resolver(chave);
        try {
            Files.createDirectories(destino.getParent());
            Files.write(destino, conteudo);
            log.info("Arquivo gravado no disco: {} ({} bytes)", destino, conteudo.length);
        } catch (IOException ex) {
            throw new StorageException("Falha ao gravar o arquivo no disco: " + destino, ex);
        }
    }

    @Override
    public byte[] ler(String chave) {
        Path origem = resolver(chave);
        try {
            if (!Files.exists(origem)) {
                throw new StorageException("Arquivo nao encontrado no disco: " + chave);
            }
            return Files.readAllBytes(origem);
        } catch (IOException ex) {
            throw new StorageException("Falha ao ler o arquivo do disco: " + origem, ex);
        }
    }

    @Override
    public void remover(String chave) {
        Path alvo = resolver(chave);
        try {
            Files.deleteIfExists(alvo);
            log.info("Arquivo removido do disco: {}", alvo);
        } catch (IOException ex) {
            throw new StorageException("Falha ao remover o arquivo do disco: " + alvo, ex);
        }
    }

    /**
     * Resolve a chave dentro do diretorio base e confere que o caminho final
     * continua sob ele — barreira final contra path traversal, caso alguma
     * chave escape da sanitizacao feita no servico.
     */
    private Path resolver(String chave) {
        Path base = Paths.get(propriedades.getLocalDir()).toAbsolutePath().normalize();
        Path destino = base.resolve(chave).normalize();
        if (!destino.startsWith(base)) {
            throw new StorageException("Caminho de arquivo invalido: " + chave);
        }
        return destino;
    }
}
