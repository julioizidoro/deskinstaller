package br.com.deskinstaller.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Ficha de atendimento (visita tecnica) realizada para um cliente em um endereco.
 *
 * @author Julio Izidoro
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fichaatendimento")
public class Fichaatendimento implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idfichaatendimento")
    private Integer idfichaatendimento;

    @Column(name = "datavisita")
    private LocalDate datavisita;

    // Hora da visita no formato HH:mm (texto curto, 5 chars).
    @Column(name = "horavisita", length = 5)
    private String horavisita;

    // Situacao da ficha (ex.: ABERTA, CONCLUIDA, CANCELADA).
    @Column(name = "situacao", length = 20)
    private String situacao;

    // MySQL reporta TINYTEXT/TEXT/MEDIUMTEXT/LONGTEXT como LONGVARCHAR;
    // fixar o tipo evita falha de schema-validation contra o banco legado.
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "observacao", columnDefinition = "MEDIUMTEXT")
    private String observacao;

    // FK para Funcionario
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "funcionarioidfuncionario", referencedColumnName = "idfuncionario")
    private Funcionario funcionario;

    // FK para Cliente
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "clienteidcliente", referencedColumnName = "idcliente")
    private Cliente cliente;

    // FK para Endereco
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "enderecoidendereco", referencedColumnName = "idendereco")
    private Endereco endereco;

    // Itens/servicos da ficha. Cascade + orphanRemoval permitem salvar a ficha
    // com seus itens em uma unica operacao.
    @OneToMany(mappedBy = "fichaatendimento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    private List<Fichaatendimentoservico> servicos = new ArrayList<>();

    /** Mantem os dois lados da associacao sincronizados. */
    public void addServico(Fichaatendimentoservico item) {
        if (servicos == null) {
            servicos = new ArrayList<>();
        }
        item.setFichaatendimento(this);
        servicos.add(item);
    }
}
