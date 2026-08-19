package br.com.deskinstaller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de transferencia de dados de AgendaEmail.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaEmailDTO {

    private Integer idagendaemail;

    @NotBlank(message = "email é obrigatório")
    @Email(message = "email deve ser válido")
    @Size(max = 255, message = "email deve ter no máximo 255 caracteres")
    private String email;

    /** Opcional: ausente na criação nasce ativo, ausente na atualização preserva o valor atual. */
    private Boolean ativo;
}
