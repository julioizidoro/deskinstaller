package br.com.deskinstaller.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Troca de senha do usuario logado.
 * <p>
 * {@code username} e {@code idusuario} vem do front apenas como conferencia:
 * quem manda e o usuario autenticado no token. Se nao baterem, a requisicao e recusada.
 */
public record AlterarSenhaRequestDTO(
        @NotBlank(message = "username é obrigatório")
        String username,

        Integer idusuario,

        @NotBlank(message = "senhaAtual é obrigatória")
        String senhaAtual,

        @NotBlank(message = "novaSenha é obrigatória")
        @Size(min = 6, max = 100, message = "novaSenha deve ter entre 6 e 100 caracteres")
        String novaSenha
) {
}
