package br.com.deskinstaller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para renderização de Ordem de Serviço em HTML/PDF.
 * Usado pelo template Thymeleaf OsHTML.html
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OsDTO {

    // Cabeçalho
    private String numero;

    // Dados do Cliente
    private String endereco;
    private String bairro;
    private String cidade;
    private String clienteCodigo;
    private String clienteNome;
    private String clienteCnpj;
    private String foneCel;

    // Dados do Serviço
    private String data;
    private String hora;
    private String tecnico;


    // Lista de serviços
    private List<ServicoDTO> servicos;

    // Total
    private String total;

    /**
     * DTO interno para cada serviço da OS
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServicoDTO {
        private String descricao;
        private String aparelho;
        private String quantidade;
        private String valor;
    }
}

