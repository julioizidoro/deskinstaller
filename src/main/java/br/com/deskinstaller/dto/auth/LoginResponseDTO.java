package br.com.deskinstaller.dto.auth;

public record LoginResponseDTO(
        String token,
        String refreshToken,
        String tokenType,
        long expiresIn,
        long refreshExpiresIn,
        String username
) {
}
