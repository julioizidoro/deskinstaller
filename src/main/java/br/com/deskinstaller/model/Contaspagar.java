/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.deskinstaller.model;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Wolverine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contaspagar")
public class Contaspagar implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcontasPagar")
    private Integer idcontasPagar;
    @Column(name = "numeroDocumento")
    private String numeroDocumento;
    @Column(name = "dataLancamento")
    @Temporal(TemporalType.DATE)
    private Date dataLancamento;
    @Column(name = "dataVencimento")
    @Temporal(TemporalType.DATE)
    private Date dataVencimento;
    @Column(name = "dataReal")
    @Temporal(TemporalType.DATE)
    private Date dataReal;
    @Column(name = "credor")
    private String credor;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valorConta")
    private Double valorConta;
    @Column(name = "valorParcial")
    private Double valorParcial;
    @Column(name = "tipoPagamento")
    private String tipoPagamento;
    @Column(name = "observacao")
    private String observacao;
    @Column(name = "idFornecedor")
    private Integer idFornecedor;
    @Column(name = "boletoEntregue")
    private Integer boletoEntregue;
    @Column(name = "mes")
    private String mes;
    @Column(name = "pagamentoContasPagar_idpagamentoContasPagar")
    private int pagamentocontaspagar;
    @Column(name = "planoconta_idplanoconta")
    private int planoconta;
    @Column(name = "pagamento")
    private String pagamento;
    @Column(name = "futura")
    private String futura;


    
}
