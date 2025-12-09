package br.com.deskinstaller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObsTecnicoDTO implements Serializable {

    private Integer idobsTecnico;
    private String observacao;
    private FuncionarioDTO funcionario;
    private Integer ordemServico;
    private LocalDateTime datahora;
    private Boolean ativa;
}
