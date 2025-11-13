/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.deskinstaller.model;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

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
@Table(name = "controlecheques")
public class Controlecheques implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idControleCheques")
    private Integer idControleCheques;
    @Column(name = "banco")
    private String banco;
    @Column(name = "agencia")
    private String agencia;
    @Column(name = "contaCorrente")
    private String contaCorrente;
    @Column(name = "numeroCheque")
    private String numeroCheque;
    @Column(name = "dataEmissao")
    @Temporal(TemporalType.DATE)
    private Date dataEmissao;
    @Column(name = "dataCompensacao")
    @Temporal(TemporalType.DATE)
    private Date dataCompensacao;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valorCheque")
    private Double valorCheque;
    @Column(name = "situacao")
    private String situacao;
    @Column(name = "local")
    private String local;
    @Column(name = "chequeTroca")
    private String chequeTroca;
    @Column(name = "preDatado")
    private String preDatado;
    @Column(name = "observacao")
    private String observacao;
    @Column(name = "cliente_idcliente")
    private int cliente;

}
