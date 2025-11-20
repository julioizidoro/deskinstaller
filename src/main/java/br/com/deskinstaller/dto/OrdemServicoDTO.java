package br.com.deskinstaller.dto;

import java.io.Serializable;
import java.util.Date;

import br.com.deskinstaller.model.Cliente;
import br.com.deskinstaller.model.Funcionario;
import br.com.deskinstaller.model.Funcionario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferência de dados de Ordemservico.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdemservicoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idordemServico;
    private String horaServico;
    private Date dataServico;
    private double valor;
    private String observacao;
    private String situacao;
    private Double valorComissao;
    private Funcionario Funcionario;
    private Cliente cliente;
    private Funcionario funcionario;
    private String indicacao;
}

