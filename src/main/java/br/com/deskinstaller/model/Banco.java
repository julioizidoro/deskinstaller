/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.deskinstaller.model;

import java.io.Serializable;
import java.util.List;
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
@Table(name = "banco")
public class Banco implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idbanco")
    private Integer idbanco;
    @Column(name = "codigoBanco")
    private String codigoBanco;
    @Column(name = "numeroAgencia")
    private String numeroAgencia;
    @Column(name = "nuneroConta")
    private String nuneroConta;
    @Column(name = "nomeAgencia")
    private String nomeAgencia;
    @Column(name = "telefoneAgencia")
    private String telefoneAgencia;
    @Column(name = "contato")
    private String contato;
    @Column(name = "empresa_idempresa")
    private int empresa;



}
