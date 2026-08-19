package br.com.deskinstaller.model;

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

import java.io.Serializable;

/**
 * Endereco de e-mail que recebe as agendas enviadas pelo sistema.
 *
 * <p>Registros nunca sao removidos: para tirar um endereco de circulacao,
 * marque {@code ativo = false}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "agendaemail")
public class AgendaEmail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idagendaemail")
    private Integer idagendaemail;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "ativo")
    private Boolean ativo;
}
