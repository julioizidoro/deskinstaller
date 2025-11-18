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
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
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
@Table(name = "orcamento")
@NamedQueries({
    @NamedQuery(name = "Orcamento.findAll", query = "SELECT o FROM Orcamento o")})
public class Orcamento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idorcamento")
    private Integer idorcamento;
    @Column(name = "horaServico")
    private String horaServico;
    @Column(name = "dataservico")
    @Temporal(TemporalType.DATE)
    private Date dataservico;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private Float valor;
    @Column(name = "formaPagamento")
    private String formaPagamento;
    @Lob
    @Column(name = "observacao")
    private String observacao;
    @Column(name = "Funcionario_idFuncionario")
    private int Funcionario;
    @Column(name = "cliente_idcliente")
    private int cliente;
    @Column(name = "situacao")
    private String situacao;
    @Column(name = "indicacao")
    private String indicacao;
    @Column(name = "status")
    private String status;
    @Column(name = "funcionario_idfuncionario")
    private int funcionario;


}
