package br.com.deskinstaller.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "username é obrigatório")
        String username,
        @NotBlank(message = "password é obrigatória")
        String password
) {
}
