package br.com.deskinstaller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO para transferência de dados de Cliente
 *
 * @author Julio Izidoro
 * @since 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {

    private Integer idcliente;

    @NotBlank(message = "nome é obrigatório")
    private String nome;

    @NotBlank(message = "tipoPessoa é obrigatório")
    private String tipoPessoa;

    @NotNull(message = "dataNascimento é obrigatória")
    private Date dataNascimento;
    private String foneResidencial;
    private String foneCelular;
    private String foneComercial;

    @Email(message = "email deve ser válido")
    private String email;
    private String contato;
    private String cpfcnpj;
    private String rgie;
}
