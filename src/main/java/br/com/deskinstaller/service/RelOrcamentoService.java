package br.com.deskinstaller.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.deskinstaller.dto.RelOrcamentoDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.Relorcamento;
import br.com.deskinstaller.repository.OrcamentoRepository;
import br.com.deskinstaller.repository.RelOrcamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Regras de negocio dos itens de orcamento.
 *
 * @author Julio Izidoro
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RelOrcamentoService {

    private final RelOrcamentoRepository relOrcamentoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final DomainValidationService domainValidationService;
    private final ServicoService servicoService;

    @Transactional(readOnly = true)
    public List<RelOrcamentoDTO> listarPorOrcamento(Integer idorcamento) {
        return relOrcamentoRepository.findByOrcamento(idorcamento).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<RelOrcamentoDTO> buscarPorId(Integer id) {
        return relOrcamentoRepository.findById(id).map(this::converterParaDTO);
    }

    @Transactional
    public RelOrcamentoDTO salvar(RelOrcamentoDTO dto) {
        if (dto.getOrcamento() == null) {
            throw new IllegalArgumentException("orcamento é obrigatório");
        }
        if (!orcamentoRepository.existsById(dto.getOrcamento())) {
            throw new ResourceNotFoundException("Orçamento não encontrado com ID: " + dto.getOrcamento());
        }
        if (dto.getIdrelorcamento() != null && !relOrcamentoRepository.existsById(dto.getIdrelorcamento())) {
            throw new ResourceNotFoundException("Item de orçamento não encontrado com ID: " + dto.getIdrelorcamento());
        }
        domainValidationService.requireServico(dto.getServico());

        Relorcamento salvo = relOrcamentoRepository.save(converterParaEntidade(dto));
        log.info("Item de orçamento salvo com sucesso. ID: {}", salvo.getIdrelorcamento());
        return converterParaDTO(salvo);
    }

    @Transactional
    public void deletar(Integer id) {
        if (!relOrcamentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item de orçamento não encontrado com ID: " + id);
        }
        relOrcamentoRepository.deleteById(id);
        log.info("Item de orçamento deletado. ID: {}", id);
    }

    @Transactional
    public void deletarPorOrcamento(Integer idorcamento) {
        relOrcamentoRepository.deleteByOrcamento(idorcamento);
    }

    public RelOrcamentoDTO converterParaDTO(Relorcamento e) {
        if (e == null) {
            return null;
        }
        return RelOrcamentoDTO.builder()
                .idrelorcamento(e.getIdrelorcamento())
                .descricao(e.getDescricao())
                .quantidade(e.getQuantidade())
                .valor(e.getValor())
                .orcamento(e.getOrcamento())
                .servico(e.getServico())
                .servicoDados(servicoService.buscarPorId(e.getServico()).orElse(null))
                .build();
    }

    public Relorcamento converterParaEntidade(RelOrcamentoDTO dto) {
        Relorcamento e = new Relorcamento();
        e.setIdrelorcamento(dto.getIdrelorcamento());
        e.setDescricao(dto.getDescricao());
        e.setQuantidade(dto.getQuantidade());
        e.setValor(dto.getValor());
        e.setOrcamento(dto.getOrcamento());
        e.setServico(dto.getServico());
        return e;
    }
}
