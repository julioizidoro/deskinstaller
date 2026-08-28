package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.ConfirmacaoOsDTO;
import br.com.deskinstaller.model.Cliente;
import br.com.deskinstaller.model.Endereco;
import br.com.deskinstaller.model.Ordemservico;
import br.com.deskinstaller.model.Relservico;
import br.com.deskinstaller.repository.OrdemServicoRepository;
import br.com.deskinstaller.repository.RelServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Monta os dados publicos da OS usados pelas telas de agendamento do cliente.
 *
 * <p>So leitura: nem confirma nem cancela nada.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmacaoOsService {

    private final DomainValidationService domainValidationService;
    private final OrdemServicoRepository ordemServicoRepository;
    private final RelServicoRepository relServicoRepository;

    @Transactional(readOnly = true)
    public ConfirmacaoOsDTO buscarPorId(Integer idOrdemServico) {
        return converter(domainValidationService.requireOrdemServico(idOrdemServico));
    }

    /**
     * Agenda de uma data, cada OS ja com os seus servicos.
     *
     * <p>Uma consulta de servicos por OS: a agenda do dia tem poucas ordens e
     * assim o chamador nao precisa de uma chamada extra por OS.
     */
    @Transactional(readOnly = true)
    public List<ConfirmacaoOsDTO> listarPorData(LocalDate data) {
        LocalDate dia = data != null ? data : LocalDate.now();
        Date inicio = Date.from(dia.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fim = Date.from(dia.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        List<Ordemservico> ordens =
                ordemServicoRepository.findByDataServicoBetweenFetchClienteAndEndereco(inicio, fim);

        log.info("Agenda de {}: {} OS", dia, ordens.size());
        return ordens.stream().map(this::converter).collect(Collectors.toList());
    }

    private ConfirmacaoOsDTO converter(Ordemservico os) {
        Cliente cliente = os.getCliente();
        Endereco endereco = os.getEndereco();
        List<Relservico> servicos = relServicoRepository.findByOrdemservico(os.getIdordemServico());

        return ConfirmacaoOsDTO.builder()
                .numeroos(os.getIdordemServico())
                .idordemServico(os.getIdordemServico())
                .clienteNome(cliente != null ? cliente.getNome() : null)
                .dataServico(os.getDataServico())
                .horaServico(os.getHoraServico())
                .situacao(os.getSituacao())
                .statuscliente(os.getStatuscliente())
                .valor(os.getValor())
                .observacao(os.getObservacao())
                .logradouro(endereco != null ? endereco.getLogradouro() : null)
                .numero(endereco != null ? endereco.getNumero() : null)
                .complemento(endereco != null ? endereco.getComplemento() : null)
                .bairro(endereco != null ? endereco.getBairro() : null)
                .cidade(endereco != null ? endereco.getCidade() : null)
                .estado(endereco != null ? endereco.getEstado() : null)
                .cep(endereco != null ? endereco.getCep() : null)
                .pontoReferencia(endereco != null ? endereco.getPontoReferencia() : null)
                .servicos(servicos == null ? List.of() : servicos.stream()
                        .map(this::converterServico)
                        .collect(Collectors.toList()))
                .build();
    }

    private ConfirmacaoOsDTO.ServicoItem converterServico(Relservico item) {
        String descricao = item.getServico() != null ? item.getServico().getDescricao() : null;
        if (item.getDescricao() != null && !item.getDescricao().isBlank()) {
            descricao = descricao == null || descricao.isBlank()
                    ? item.getDescricao()
                    : descricao + ": " + item.getDescricao();
        }
        return ConfirmacaoOsDTO.ServicoItem.builder()
                .descricao(descricao)
                .quantidade(item.getQuantidade())
                .valor(item.getValor())
                .build();
    }
}
