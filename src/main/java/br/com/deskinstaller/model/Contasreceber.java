package br.com.deskinstaller.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Titulo a receber. Pode estar ligado a uma ou mais ordens de servico
 * atraves de {@link Contasreceberos}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contasreceber")
public class Contasreceber implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcontasreceber")
    private Integer idcontasreceber;

    @Column(name = "dataemissao")
    private LocalDate dataemissao;

    @Column(name = "numero", length = 50)
    private String numero;

    // As colunas de valor sao DECIMAL(10,2) no banco; BigDecimal evita erro de
    // schema-validation (Double seria validado como FLOAT) e perda de precisao.
    @Column(name = "valorreceber", precision = 10, scale = 2)
    private BigDecimal valorreceber;

    @Column(name = "datavencimento")
    private LocalDate datavencimento;

    @Column(name = "valorrecebido", precision = 10, scale = 2)
    private BigDecimal valorrecebido;

    // Data em que o titulo foi efetivamente recebido; nulo enquanto em aberto.
    @Column(name = "datarecebimento")
    private LocalDate datarecebimento;

    @Column(name = "valorjuros", precision = 10, scale = 2)
    private BigDecimal valorjuros;

    @Column(name = "valordesconto", precision = 10, scale = 2)
    private BigDecimal valordesconto;

    // MySQL reporta MEDIUMTEXT como LONGVARCHAR; fixar o tipo evita falha de
    // schema-validation contra o banco legado.
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "observacao")
    private String observacao;

    @Column(name = "numeronf", length = 45)
    private String numeronf;

    // Usuario responsavel pelo lancamento. Coluna escalar (sem @ManyToOne),
    // seguindo o padrao dos demais vinculos do schema legado.
    @Column(name = "usuarioidusuario", nullable = false)
    private Integer usuarioidusuario;

    // Cliente devedor do titulo.
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "clienteidcliente", referencedColumnName = "idcliente", nullable = false)
    private Cliente cliente;
}
