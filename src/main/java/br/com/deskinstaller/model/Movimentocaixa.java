/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.deskinstaller.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

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
@Table(name = "movimentocaixa")
public class Movimentocaixa implements Serializable {
    @Column(name =     "dataMovimento")
    @Temporal(TemporalType.DATE)
    private Date dataMovimento;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idmovimentoCaixa")
    private Integer idmovimentoCaixa;
    @Column(name = "descricao")
    private String descricao;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valorEntrada")
    private Float valorEntrada;
    @Column(name = "valorSaida")
    private Float valorSaida;
    @Column(name = "planoconta_idplanoconta")
    private int planoconta;


}
