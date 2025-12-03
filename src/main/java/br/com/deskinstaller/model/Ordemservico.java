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
 * @author wolverine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ordemservico")
public class Ordemservico implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idordemServico")
    private Integer idordemServico;
    @Basic(optional = false)
    @Column(name = "horaServico")
    private String horaServico;
    @Basic(optional = false)
    @Column(name = "dataServico")
    @Temporal(TemporalType.DATE)
    private Date dataServico;
    @Basic(optional = false)
    @Column(name = "valor")
    private double valor;
    @Lob
    @Column(name = "observacao")
    private String observacao;
    @Column(name = "situacao")
    private String situacao;
    @Temporal(TemporalType.DATE)
    private Date datasituacao;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valorComissao")
    private Double valorComissao;

    // Relacionamento com Cliente: adicionar @ManyToOne para que JPA reconheça a associação
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "cliente_idcliente", referencedColumnName = "idcliente")
    private Cliente cliente;

    // Relacionamento com Endereco
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "endereco_idendereco", referencedColumnName = "idendereco")
    private Endereco endereco;
    @Column(name = "indicacao")
    private String indicacao;
    @Column(name = "recebida")
    private boolean recebida;


}
