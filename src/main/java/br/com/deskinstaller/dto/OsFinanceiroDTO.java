/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.deskinstaller.dto;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Wolverine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OsFinanceiroDTO implements Serializable {

    private Integer idosfinanceiro;
    private LocalDate data;
    private int parcelas;
    private Float valordesconto;
    private Float valorrecebido;
    private String formapagamento;

    @NotNull(message = "ordemservico é obrigatória")
    private Integer ordemservico;
}
