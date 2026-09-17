package br.com.deskinstaller.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de dados do Orcamento.
 *
 * @author Julio Izidoro
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrcamentoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idorcamento;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/Sao_Paulo")
    private Date dataemissao;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/Sao_Paulo")
    private Date datavalidade;

    private Float valor;

    @Size(max = 45, message = "formaPagamento deve ter no maximo 45 caracteres")
    private String formaPagamento;

    private String observacao;

    @NotNull(message = "cliente e obrigatorio")
    private Integer cliente;

    private Integer endereco;

    @Size(max = 45, message = "situacao deve ter no maximo 45 caracteres")
    private String situacao;

    /** Dados apenas de leitura, preenchidos na resposta. */
    private ClienteDTO clienteDados;
    private EnderecoDTO enderecoDados;

    /** Itens do orcamento. Opcional no envio. */
    @Valid
    @Builder.Default
    private List<RelOrcamentoDTO> itens = new ArrayList<>();
}
