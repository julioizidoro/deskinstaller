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
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valorComissao")
    private Double valorComissao;
    @Column(name = "endereco_idendereco")
    private int endereco;
    @Column(name = "cliente_idcliente")
    private int cliente;
    @Column(name = "funcionario_idfuncionario")
    private int funcionario;
    @Column(name = "indicacao")
    private String indicacao;


}
