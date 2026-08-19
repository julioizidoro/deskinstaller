package br.com.deskinstaller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.time.Instant;

public record ApiErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path,
        Object details
) {

    public static ApiErrorResponse of(HttpStatusCode statusCode, String message, String path, Object details) {
        String error = statusCode.toString();
        if (statusCode instanceof HttpStatus httpStatus) {
            error = httpStatus.getReasonPhrase();
        }

        return new ApiErrorResponse(
                Instant.now().toString(),
                statusCode.value(),
                error,
                message,
                path,
                details
        );
    }
}
