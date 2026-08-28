/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.deskinstaller.model;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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
@Table(name = "ordemservico")
public class Ordemservico implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idordemServico")
    private Integer idordemServico;
    @Basic(optional = false)
    @Column(name = "horaServico")
    private String horaServico;
    @Basic(optional = false)
    @Column(name = "dataServico")
    @Temporal(TemporalType.DATE)
    private Date dataServico;
    @Basic(optional = false)
    @Column(name = "valor")
    private double valor;
    // MySQL reporta TINYTEXT/TEXT/MEDIUMTEXT/LONGTEXT como LONGVARCHAR;
    // fixar o tipo evita falha de schema-validation contra o banco legado.
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "observacao")
    private String observacao;
    @Column(name = "situacao")
    private String situacao;
    @Temporal(TemporalType.DATE)
    private Date datasituacao;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valorComissao")
    private Double valorComissao;

    // Relacionamento com Cliente: adicionar @ManyToOne para que JPA reconheça a associação
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "cliente_idcliente", referencedColumnName = "idcliente")
    private Cliente cliente;

    // Relacionamento com Endereco
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "endereco_idendereco", referencedColumnName = "idendereco")
    private Endereco endereco;
    @Column(name = "indicacao")
    private String indicacao;
    @Column(name = "recebida")
    private boolean recebida;

    // E-mail convidado para o evento da agenda referente a esta OS.
    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "googleEventId", length = 255)
    private String googleEventId;

    // Usuario que registrou/e responsavel pela OS. Coluna escalar (sem FK),
    // seguindo o padrao dos demais vinculos legados do schema.
    @Column(name = "usuarioidusuario")
    private Integer usuarioidusuario;

    // Status informado pelo cliente na tela publica de agendamento.
    @Column(name = "statuscliente", length = 15)
    private String statuscliente;


}
