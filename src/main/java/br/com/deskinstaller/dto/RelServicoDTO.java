package br.com.deskinstaller.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelServicoDTO {

    private Integer idrelServico;
    private String descricao;
    private double quantidade;
    private double valor;

    @NotNull(message = "servico é obrigatório")
    private ServicoDTO servico;

    @NotNull(message = "ordemservico é obrigatória")
    private Integer ordemservico;

    @NotNull(message = "apCliente é obrigatório")
    private ApclienteDTO apCliente;
    private boolean situacao;
}
