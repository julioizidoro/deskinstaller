package br.com.deskinstaller.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Linha da tabela de recebimentos do recibo da OS.
 * As datas ja chegam formatadas para o template nao depender de dialeto de data.
 *
 * @author Julio Izidoro
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReciboParcelaDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Numero do titulo ou da parcela (ex.: "3/5"). */
    private String documento;
    private String vencimento;
    private String recebimento;
    private String formaPagamento;
    private BigDecimal valor;
}
