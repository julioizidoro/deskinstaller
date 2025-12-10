/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.deskinstaller.model;

import java.io.Serializable;
import java.time.LocalDate;

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
@Table(name = "osfinanceiro")
public class OsFinanceiro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idosfinanceiro")
    private Integer idosfinanceiro;
    @Column(name = "data")
    private LocalDate data;
    @Column(name = "parcelas")
    private int parcelas;
    @Column(name = "valordesconto")
    private Float valordesconto;
    @Column(name = "valorrecebido")
    private Float valorrecebido;
    @Column(name = "formapagamento")
    private String formapagamento;
    @Column(name = "ordemservico_idordemservico")
    private int ordemservico;



}
