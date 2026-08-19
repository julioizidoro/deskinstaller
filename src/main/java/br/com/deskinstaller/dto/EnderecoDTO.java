package br.com.deskinstaller.dto;

import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "logradouro é obrigatório")
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;

    @NotBlank(message = "cidade é obrigatória")
    private String cidade;
    private String estado;
    private String pontoReferencia;
    private String foneInstalacao;
    private String idmaps;
    private Boolean ativo;

    @NotNull(message = "cliente é obrigatório")
    private Integer cliente;
}
