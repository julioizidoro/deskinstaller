package br.com.deskinstaller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

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
public class OrdemServicoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idordemServico;

    @NotBlank(message = "horaServico é obrigatória")
    private String horaServico;

    @NotNull(message = "dataServico é obrigatória")
    private Date dataServico;
    private double valor;
    private String observacao;
    private String situacao;
    private Date datasituacao;
    private Double valorComissao;

    @NotNull(message = "clienteId é obrigatório")
    private Integer clienteId;

    private String clienteNome;

    private Integer enderecoId;
    private String enderecoResumo;
    private String indicacao;
    private boolean recebida;
}
