package br.com.deskinstaller.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnderecoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idendereco;
    private String tipoLogradouro;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private String cidade;
    private String estado;
    private String pontoReferencia;
    private String idmaps;
    private Boolean ativo;
    private Integer cliente;
}
