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
    @Lob
    @Column(name = "descricao", columnDefinition = "MEDIUMTEXT")
    private String descricao;
    @Basic(optional = false)
    @Column(name = "quantidade")
    private double quantidade;
    @Basic(optional = false)
    @Column(name = "valor")
    private double valor;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)

    // FK para Servico
    @JoinColumn(name = "servico_idservico", referencedColumnName = "idservico")
    private Servico servico;
    @Column(name = "ordemServico_idordemServico")
    private int ordemservico;

    // FK para ApCliente
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "apcliente_idpacliente", referencedColumnName = "idapCliente")
    private Apcliente apCliente;

    @Basic(optional = false)
    @Column(name = "situacao")
    private boolean situacao;


}
