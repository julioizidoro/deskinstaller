package br.com.deskinstaller.dto;

import java.io.Serializable;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de item de servico da ficha de atendimento.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FichaAtendimentoServicoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idfichaatendimentosdrvico;

    private Double quantidade;

    @Size(max = 255, message = "descricao deve ter no maximo 255 caracteres")
    private String descricao;

    /** Id da ficha. Opcional quando o item vem aninhado dentro da ficha. */
    private Integer fichaatendimento;
}
