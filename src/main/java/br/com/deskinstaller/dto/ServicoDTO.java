package br.com.deskinstaller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoDTO {

    private Integer idservico;

    @NotBlank(message = "descricao é obrigatória")
    private String descricao;
    private boolean situacao;
}
