package br.com.deskinstaller.model;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade Grupoconta
 * @author Wolverine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grupoconta")
public class Grupoconta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idgrupoConta")
    private Integer idgrupoConta;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "numeroConta")
    private String numeroConta;

    @Column(name = "numeroSubGrupo")
    private Integer numeroSubGrupo;
}

