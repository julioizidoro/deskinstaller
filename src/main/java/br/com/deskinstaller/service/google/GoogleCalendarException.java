package br.com.deskinstaller.service.google;

/**
 * Falha na comunicacao com o Google Calendar.
 *
 * <p>Nunca deve interromper o fluxo de negocio da ordem de servico: quem chama
 * a integracao e responsavel por registrar o erro e seguir adiante.
 */
public class GoogleCalendarException extends RuntimeException {

    public GoogleCalendarException(String mensagem) {
        super(mensagem);
    }

    public GoogleCalendarException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
