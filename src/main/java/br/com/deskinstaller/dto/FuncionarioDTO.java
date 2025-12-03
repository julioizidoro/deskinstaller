package br.com.deskinstaller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO leve para transferência de dados do Funcionario (evita serializar entidades Hibernate)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuncionarioDTO implements Serializable {
    private Integer idfuncionario;
    private String nome;
    private String foneCelular;
    private Float valorComissao;
    private String funcao;
    private Boolean ativo;
}

