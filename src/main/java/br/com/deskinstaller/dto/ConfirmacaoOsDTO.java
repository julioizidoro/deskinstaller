package br.com.deskinstaller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Dados publicos de uma OS mostrados nas telas de agendamento do cliente
 * ({@code /confirmacao/{id}} e {@code /cancelamento/{id}} no front).
 *
 * <p>Os nomes dos campos espelham a interface {@code ConfirmacaoOs} do Angular:
 * mudar um nome aqui quebra a tela.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmacaoOsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer numeroos;
    private Integer idordemServico;
    private String clienteNome;
    private Date dataServico;
    private String horaServico;
    private String situacao;
    private String statuscliente;
    private Double valor;
    private String observacao;

    private String enderecoResumo;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private String pontoReferencia;

    private List<ServicoItem> servicos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServicoItem implements Serializable {
        private static final long serialVersionUID = 1L;

        private String descricao;
        private Double quantidade;
        private Double valor;
    }
}
