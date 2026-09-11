package br.com.deskinstaller.service.storage;

import java.util.Optional;

/**
 * Contrato generico de armazenamento de binarios.
 *
 * <p>A chave e sempre o caminho completo dentro do repositorio
 * (ex.: {@code deskinstaller/dev/cliente/1956/os/5455/uuid.pdf}). Quem monta a
 * chave e o servico de arquivos; a implementacao apenas grava onde sabe.
 */
public interface FileStorage {

    /** Identificacao gravada na tabela: S3 ou LOCAL. */
    String tipo();

    /** false quando falta configuracao (credencial, bucket, diretorio). */
    boolean disponivel();

    void gravar(String chave, byte[] conteudo, String contentType);

    byte[] ler(String chave);

    void remover(String chave);

    /**
     * URL temporaria de download, quando o storage souber gerar uma.
     * O provider local devolve {@link Optional#empty()} — nesse caso o download
     * sai pelo proprio endpoint da API.
     */
    default Optional<String> urlAssinada(String chave, String nomeParaDownload) {
        return Optional.empty();
    }
}
