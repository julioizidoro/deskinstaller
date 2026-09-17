package br.com.deskinstaller.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de item (servico) de um orcamento.
 *
 * @author Julio Izidoro
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelOrcamentoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idrelorcamento;

    @Size(max = 100, message = "descricao deve ter no maximo 100 caracteres")
    private String descricao;

    private Double quantidade;

    private float valor;

    /** Id do orcamento. Opcional quando o item vem aninhado dentro do orcamento. */
    private Integer orcamento;

    @NotNull(message = "servico e obrigatorio")
    private Integer servico;

    /** Dados apenas de leitura, preenchidos na resposta. */
    private ServicoDTO servicoDados;
}
