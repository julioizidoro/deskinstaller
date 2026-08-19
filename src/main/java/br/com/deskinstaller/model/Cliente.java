package br.com.deskinstaller.model;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade Cliente
 * @author wolverine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cliente")
@Access(AccessType.FIELD)
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcliente")
    private Integer idcliente;

    @Basic(optional = false)
    @Column(name = "nome")
    private String nome;

    @Basic(optional = false)
    @Column(name = "tipoPessoa")
    private String tipoPessoa;

    @Basic(optional = false)
    @Column(name = "dataNascimento")
    @Temporal(TemporalType.DATE)
    private Date dataNascimento;

    @Column(name = "foneResidencial")
    private String foneResidencial;

    @Column(name = "foneCelular")
    private String foneCelular;

    @Column(name = "foneComercial")
    private String foneComercial;

    @Column(name = "email")
    private String email;

    @Column(name = "contato")
    private String contato;

    @Column(name = "cpfcnpj")
    private String cpfcnpj;

    @Column(name = "rgie")
    private String rgie;
}
