package br.com.deskinstaller.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Linha da tabela de servicos executados do recibo da OS.
 *
 * @author Julio Izidoro
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReciboServicoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Descricao do servico (ex.: "Manutenção Preventiva"). */
    private String servico;

    /** Fabricante + modelo + capacidade do aparelho (ex.: "LG Split Hw Inverter 18.000 Btu/h"). */
    private String equipamento;

    /** Comodo/local onde o aparelho esta instalado (ex.: "Sala"). */
    private String local;

    private String quantidade;
}
