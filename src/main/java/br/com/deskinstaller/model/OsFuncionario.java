
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
@Table(name = "osfuncionario")
public class OsFuncionario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idosFuncionario")
    private Integer idosFuncionario;
    @Column(name = "ordemservico_idordemservico")
    private Integer ordemServico;

    // FK para Funcionario
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "funcionario_idfuncionario", referencedColumnName = "idfuncionario")
    private Funcionario funcionario;
}

