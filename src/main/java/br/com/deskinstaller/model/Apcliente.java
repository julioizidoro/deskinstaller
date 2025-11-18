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
@Table(name = "apcliente")
public class Apcliente implements Serializable {
    @Column(name = "dataCompra")
    @Temporal(TemporalType.DATE)
    private Date dataCompra;
    @Column(name = "notaFiscal")
    private String notaFiscal;
    @Column(name = "vendedor_idvendedor")
    private int vendedor;
    @Column(name = "loja_idloja")
    private int loja;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idapCliente")
    private Integer idapCliente;
    @Column(name = "dataInstalacao")
    @Temporal(TemporalType.DATE)
    private Date dataInstalacao;
    @Column(name = "dataManutencao")
    @Temporal(TemporalType.DATE)
    private Date dataManutencao;
    @Column(name = "local")
    private String local;
    @Column(name = "cliente_idcliente")
    private int cliente;
    @Column(name = "Funcionario_idFuncionario")
    private int Funcionario;
    @Column(name = "modelo")
    private String modelo;
    @Column(name = "fabricante")
    private String fabricante;
    @Column(name = "modeloEvaporadora")
    private String modeloEvaporadora;
    @Column(name = "nsEvaporadora")
    private String nsEvaporadora;
    @Column(name = "modeloCodensadora")
    private String modeloCodensadora;
    @Column(name = "nsCodensadora")
    private String nsCodensadora;
     @Column(name = "capacidade")
    private String capacidade;
}
