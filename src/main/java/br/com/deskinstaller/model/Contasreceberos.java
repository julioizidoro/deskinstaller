package br.com.deskinstaller.model;

import java.io.Serializable;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vinculo entre um titulo a receber e uma ordem de servico.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contasreceberos")
public class Contasreceberos implements Serializable {
    private static final long serialVersionUID = 1L;

    // A chave primaria da tabela chama-se "contasreceberos", igual a tabela.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "contasreceberos")
    private Integer idcontasreceberos;

    @Column(name = "contasreceberidcontasreceber", nullable = false)
    private Integer contasreceberidcontasreceber;

    @Column(name = "ordemservicoidordemServico", nullable = false)
    private Integer ordemservicoidordemServico;
}
