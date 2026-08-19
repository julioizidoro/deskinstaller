package br.com.deskinstaller.service.whatsapp;

/**
 * Falha na comunicacao com o servidor de WhatsApp.
 *
 * <p>Espelha o {@code WhatsAppApiHttpError} da versao Node: carrega o status
 * HTTP e o corpo devolvido pelo servidor, quando houver.
 */
public class WhatsAppApiException extends RuntimeException {

    private final Integer status;
    private final transient Object corpo;

    public WhatsAppApiException(String mensagem, Integer status, Object corpo, Throwable causa) {
        super(mensagem, causa);
        this.status = status;
        this.corpo = corpo;
    }

    public WhatsAppApiException(String mensagem, Throwable causa) {
        this(mensagem, null, null, causa);
    }

    public WhatsAppApiException(String mensagem) {
        this(mensagem, null, null, null);
    }

    public Integer getStatus() {
        return status;
    }

    public Object getCorpo() {
        return corpo;
    }
}
