package br.com.deskinstaller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO para transferência de dados de Cliente
 *
 * @author Julio Izidoro
 * @since 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {

    private Integer idcliente;
    private String nome;
    private String tipoPessoa;
    private Date dataNascimento;
    private String foneResidencial;
    private String foneCelular;
    private String foneComercial;
    private String email;
    private String contato;
    private String cpfcnpj;
    private String rgie;
}

