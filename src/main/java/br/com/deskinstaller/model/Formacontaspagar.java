/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.deskinstaller.model;

import java.io.Serializable;
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
@Table(name = "formacontaspagar")
public class Formacontaspagar implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idformaContasPagar")
    private Integer idformaContasPagar;
    @Column(name = "formaPagamento")
    private String formaPagamento;
    @Column(name = "idBanco")
    private Integer idBanco;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valorPago")
    private Double valorPago;
    @Column(name = "pagamentoContasPagar_idpagamentoContasPagar")
    private int pagamentocontaspagar;


    
}
