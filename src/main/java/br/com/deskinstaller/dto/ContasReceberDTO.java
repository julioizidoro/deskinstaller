package br.com.deskinstaller.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferência de dados de Contasreceber.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContasReceberDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idcontasreceber;

    private LocalDate dataemissao;

    @Size(max = 50, message = "numero deve ter no máximo 50 caracteres")
    private String numero;

    private BigDecimal valorreceber;
    private LocalDate datavencimento;
    private BigDecimal valorrecebido;
    private LocalDate datarecebimento;
    private BigDecimal valorjuros;
    private BigDecimal valordesconto;
    private String observacao;

    @Size(max = 45, message = "numeronf deve ter no máximo 45 caracteres")
    private String numeronf;

    /** Preenchido pelo servidor a partir do usuário autenticado; o que vier do cliente é ignorado. */
    private Integer usuarioidusuario;

    /** Usado na escrita: identifica o cliente do título. */
    @NotNull(message = "clienteidcliente é obrigatório")
    private Integer clienteidcliente;

    /** Preenchido apenas na leitura: o cliente completo, para o front não precisar de outra chamada. */
    private ClienteDTO cliente;
}
