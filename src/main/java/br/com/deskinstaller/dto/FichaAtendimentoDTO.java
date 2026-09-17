package br.com.deskinstaller.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de dados da Ficha de Atendimento.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FichaAtendimentoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idfichaatendimento;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate datavisita;

    @Size(max = 5, message = "horavisita deve ter no maximo 5 caracteres (HH:mm)")
    private String horavisita;

    @Size(max = 20, message = "situacao deve ter no maximo 20 caracteres")
    private String situacao;

    private String observacao;

    @NotNull(message = "funcionario e obrigatorio")
    private Integer funcionario;

    @NotNull(message = "cliente e obrigatorio")
    private Integer cliente;

    @NotNull(message = "endereco e obrigatorio")
    private Integer endereco;

    /** Dados apenas de leitura, preenchidos na resposta. */
    private FuncionarioDTO funcionarioDados;
    private ClienteDTO clienteDados;
    private EnderecoDTO enderecoDados;

    @Valid
    @Builder.Default
    private List<FichaAtendimentoServicoDTO> servicos = new ArrayList<>();
}
