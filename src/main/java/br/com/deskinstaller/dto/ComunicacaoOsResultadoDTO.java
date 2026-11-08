package br.com.deskinstaller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Log do envio de avisos de agendamento por WhatsApp ({@code GET /api/ordens-servico/comunicar}).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComunicacaoOsResultadoDTO {

    /** Momento em que o envio foi executado. */
    private LocalDateTime dataEnvio;

    /** Data dos serviços avisados (data atual + 1). */
    private LocalDate dataServico;

    /** Número fixo que recebeu todos os avisos (nenhum cliente é avisado nesta fase). */
    private String telefoneDestino;

    /** Quantidade de OS encontradas para a data. */
    private int totalOs;

    private int totalEnviadas;
    private int totalFalhas;

    /** Números das OS cujo aviso foi enviado. */
    private List<Integer> osEnviadas;

    private List<Falha> falhas;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Falha {
        private Integer numeroOs;
        private String motivo;
    }
}
