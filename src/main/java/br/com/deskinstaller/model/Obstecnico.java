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
import java.time.LocalDateTime;

/**
 *
 * @author Wolverine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "obstecnico")
@NamedQueries({
    @NamedQuery(name = "Obstecnico.findAll", query = "SELECT o FROM Obstecnico o")})
public class Obstecnico implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idobsTecnico")
    private Integer idobsTecnico;
    @Lob
    @Column(name = "observacao")
    private String observacao;
    // FK para Funcionario
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "funcionario_idfuncionario", referencedColumnName = "idfuncionario")
    private Funcionario funcionario;
    @Column(name = "ordemservico_idordemservico")
    private Integer ordemServico;
    @Column(name = "datahora")
    private LocalDateTime datahora;
    @Column(name = "ativo")
    private Boolean ativa;


}
