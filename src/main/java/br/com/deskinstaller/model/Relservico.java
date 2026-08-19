/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.deskinstaller.model;

import java.io.Serializable;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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
    // MySQL reporta TINYTEXT/TEXT/MEDIUMTEXT/LONGTEXT como LONGVARCHAR;
    // fixar o tipo evita falha de schema-validation contra o banco legado.
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
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
    private Integer ordemservico;

    // FK para Funcionario
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "apcliente_idapCliente", referencedColumnName = "idapCliente")
    private Apcliente apCliente;

    @Basic(optional = false)
    @Column(name = "situacao")
    private boolean situacao;


}
