package br.com.deskinstaller.model;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Item de servico de uma ficha de atendimento.
 *
 * Observacao: o nome da tabela legada e `fichaatendimentosdrvico` (com o typo
 * de origem preservado), por isso o @Table difere do nome da classe.
 *
 * @author Julio Izidoro
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fichaatendimentosdrvico")
public class Fichaatendimentoservico implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idfichaatendimentosdrvico")
    private Integer idfichaatendimentosdrvico;

    @Column(name = "quantidade")
    private Double quantidade;

    @Column(name = "descricao", length = 255)
    private String descricao;

    // FK para Fichaatendimento
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fichaatendimentoidfichaatendimento", referencedColumnName = "idfichaatendimento")
    @ToString.Exclude
    @JsonIgnore
    private Fichaatendimento fichaatendimento;
}
