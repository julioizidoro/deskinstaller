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
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

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
    @Column(name = "funcionario_idfuncionario")
    private int funcionario;
    @Column(name = "ordemservico_idordemservico")
    private Integer ordemServico;


}
