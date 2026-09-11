package br.com.deskinstaller.service.storage;

/** Falha ao gravar, ler ou remover um binario no storage. */
public class StorageException extends RuntimeException {

    public StorageException(String mensagem) {
        super(mensagem);
    }

    public StorageException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
