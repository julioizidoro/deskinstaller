/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.deskinstaller.model;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
@Table(name = "relservico")
public class Relservico implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idrelServico")
    private Integer idrelServico;
    @Basic(optional = false)
    @Column(name = "descricao")
    private String descricao;
    @Basic(optional = false)
    @Column(name = "quantidade")
    private double quantidade;
    @Basic(optional = false)
    @Column(name = "valor")
    private double valor;
    @Column(name = "servico_idservico")
    private int servico;
    @Column(name = "ordemServico_idordemServico")
    private int ordemservico;
    @Column(name = "apcliente_idapCliente")
    private int apcliente;


}
