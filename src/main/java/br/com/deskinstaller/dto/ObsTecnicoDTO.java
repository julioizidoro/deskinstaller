package br.com.deskinstaller.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "observacao é obrigatória")
    private String observacao;

    @NotNull(message = "funcionario é obrigatório")
    private FuncionarioDTO funcionario;

    @NotNull(message = "ordemServico é obrigatória")
    private Integer ordemServico;
    private LocalDateTime datahora;
    private Boolean ativa;
}
