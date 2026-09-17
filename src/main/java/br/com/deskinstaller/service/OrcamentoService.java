package br.com.deskinstaller.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.deskinstaller.dto.OrcamentoDTO;
import br.com.deskinstaller.dto.RelOrcamentoDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.Orcamento;
import br.com.deskinstaller.model.Relorcamento;
import br.com.deskinstaller.repository.OrcamentoRepository;
import br.com.deskinstaller.repository.RelOrcamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Regras de negocio do Orcamento.
 *
 * @author Julio Izidoro
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;
    private final RelOrcamentoRepository relOrcamentoRepository;
    private final RelOrcamentoService relOrcamentoService;
    private final DomainValidationService domainValidationService;
    private final ClienteService clienteService;
    private final EnderecoService enderecoService;

    @Transactional(readOnly = true)
    public List<OrcamentoDTO> listarTodos() {
        return orcamentoRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<OrcamentoDTO> buscarPorId(Integer id) {
        return orcamentoRepository.findById(id).map(this::converterParaDTO);
    }

    @Transactional(readOnly = true)
    public List<OrcamentoDTO> listarPorCliente(Integer idcliente) {
        return orcamentoRepository.findByClienteOrderByDataemissaoDesc(idcliente).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrcamentoDTO> listarPorEndereco(Integer idendereco) {
        return orcamentoRepository.findByEnderecoOrderByDataemissaoDesc(idendereco).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrcamentoDTO> listarPorSituacao(String situacao) {
        return orcamentoRepository.findBySituacaoOrderByDataemissaoDesc(situacao).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrcamentoDTO> listarPorPeriodo(Date inicio, Date fim) {
        return orcamentoRepository.findByDataemissaoBetweenOrderByDataemissaoDesc(inicio, fim).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrcamentoDTO salvar(OrcamentoDTO dto) {
        log.info("Salvando orçamento do cliente {}", dto.getCliente());

        if (dto.getIdorcamento() != null && !orcamentoRepository.existsById(dto.getIdorcamento())) {
            throw new ResourceNotFoundException("Orçamento não encontrado com ID: " + dto.getIdorcamento());
        }

        domainValidationService.requireCliente(dto.getCliente());
        if (dto.getEndereco() != null) {
            // Garante que o endereco informado pertence ao cliente do orcamento.
            domainValidationService.validateEnderecoDoCliente(dto.getEndereco(), dto.getCliente());
        }

        Orcamento salvo = orcamentoRepository.save(converterParaEntidade(dto));

        // Itens enviados junto substituem os atuais.
        if (dto.getItens() != null && !dto.getItens().isEmpty()) {
            relOrcamentoRepository.deleteByOrcamento(salvo.getIdorcamento());
            for (RelOrcamentoDTO item : dto.getItens()) {
                item.setIdrelorcamento(null);
                item.setOrcamento(salvo.getIdorcamento());
                relOrcamentoService.salvar(item);
            }
        }

        log.info("Orçamento salvo com sucesso. ID: {}", salvo.getIdorcamento());
        return converterParaDTO(salvo);
    }

    @Transactional
    public void deletar(Integer id) {
        if (!orcamentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Orçamento não encontrado com ID: " + id);
        }
        relOrcamentoRepository.deleteByOrcamento(id);
        orcamentoRepository.deleteById(id);
        log.info("Orçamento deletado. ID: {}", id);
    }

    public OrcamentoDTO converterParaDTO(Orcamento e) {
        if (e == null) {
            return null;
        }
        List<RelOrcamentoDTO> itens = relOrcamentoRepository.findByOrcamento(e.getIdorcamento()).stream()
                .map(relOrcamentoService::converterParaDTO)
                .collect(Collectors.toList());

        return OrcamentoDTO.builder()
                .idorcamento(e.getIdorcamento())
                .dataemissao(e.getDataemissao())
                .datavalidade(e.getDatavalidade())
                .valor(e.getValor())
                .formaPagamento(e.getFormaPagamento())
                .observacao(e.getObservacao())
                .cliente(e.getCliente())
                .endereco(e.getEndereco())
                .situacao(e.getSituacao())
                .clienteDados(clienteService.buscarPorId(e.getCliente()).orElse(null))
                .enderecoDados(e.getEndereco() != null
                        ? enderecoService.converterParaDTO(domainValidationService.requireEndereco(e.getEndereco()))
                        : null)
                .itens(itens != null ? itens : new ArrayList<>())
                .build();
    }

    public Orcamento converterParaEntidade(OrcamentoDTO dto) {
        Orcamento e = new Orcamento();
        e.setIdorcamento(dto.getIdorcamento());
        e.setDataemissao(dto.getDataemissao());
        e.setDatavalidade(dto.getDatavalidade());
        e.setValor(dto.getValor());
        e.setFormaPagamento(dto.getFormaPagamento());
        e.setObservacao(dto.getObservacao());
        e.setCliente(dto.getCliente());
        e.setEndereco(dto.getEndereco());
        e.setSituacao(dto.getSituacao());
        return e;
    }

    /** Helper para reaproveitar em relatorios/PDF. */
    public List<Relorcamento> listarItens(Integer idorcamento) {
        return relOrcamentoRepository.findByOrcamento(idorcamento);
    }
}
