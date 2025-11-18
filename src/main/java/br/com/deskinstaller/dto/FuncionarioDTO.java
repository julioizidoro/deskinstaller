/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.deskinstaller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuncionarioDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idfuncionario;
    private String nome;
    private String foneCelular;
    private Float valorComissao;
    private String funcao;
    private boolean ativo;


}
