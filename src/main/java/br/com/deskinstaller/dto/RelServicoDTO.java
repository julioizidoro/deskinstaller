package br.com.deskinstaller.dto;


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
    private ServicoDTO servico;
    private Integer ordemservico;
    private ApclienteDTO apCliente;
    private boolean situacao;
}
