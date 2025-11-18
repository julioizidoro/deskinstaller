package br.com.deskinstaller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApclienteDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer idapCliente;
    private Date dataCompra;
    private String notaFiscal;
    private String loja;
    private Date dataInstalacao;
    private Date dataManutencao;
    private String local;
    private Integer cliente;
    private String modelo;
    private String fabricante;
    private String modeloEvaporadora;
    private String nsEvaporadora;
    private String modeloCodensadora;
    private String nsCodensadora;
    private String capacidade;
}

