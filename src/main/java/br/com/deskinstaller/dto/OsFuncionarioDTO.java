package br.com.deskinstaller.dto;

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
    private Integer ordemServico;
    private FuncionarioDTO funcionario;
}
