package br.com.deskinstaller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * DTO para transferência de dados de OsFuncionario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OsFuncionarioDTO implements Serializable {
    private Integer idosFuncionario;

    @NotNull(message = "ordemServico é obrigatória")
    private Integer ordemServico;

    @NotNull(message = "funcionario é obrigatório")
    private FuncionarioDTO funcionario;
}
