package br.com.deskinstaller.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(
        @NotBlank(message = "refreshToken é obrigatório")
        String refreshToken
) {
}
