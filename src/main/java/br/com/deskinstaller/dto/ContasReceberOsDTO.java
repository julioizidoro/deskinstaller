package br.com.deskinstaller.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferência de dados de Contasreceberos (vínculo título x OS).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContasReceberOsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idcontasreceberos;

    @NotNull(message = "contasreceberidcontasreceber é obrigatório")
    private Integer contasreceberidcontasreceber;

    @NotNull(message = "ordemservicoidordemServico é obrigatório")
    private Integer ordemservicoidordemServico;
}
