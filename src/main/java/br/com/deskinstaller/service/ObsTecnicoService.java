package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.ObsTecnicoDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.Obstecnico;
import br.com.deskinstaller.repository.ObsTecnicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service para lógica de negócio relacionada a ObsTecnico
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ObsTecnicoService {

    private final ObsTecnicoRepository obsTecnicoRepository;
    private final FuncionarioService funcionarioService;
    private final DomainValidationService domainValidationService;

    @Transactional(readOnly = true)
    public List<ObsTecnicoDTO> listarOS(Integer idOrdemServico) {
        List<Obstecnico> list = obsTecnicoRepository.findByOrdemServico(idOrdemServico);
        return list.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ObsTecnicoDTO> buscarPorId(Integer id) {
        return obsTecnicoRepository.findById(id).map(this::converterParaDTO);
    }

    @Transactional
    public ObsTecnicoDTO salvar(ObsTecnicoDTO dto) {
        log.info("Salvando ObsTecnico: {}", dto);
        if (dto.getIdobsTecnico() != null && !obsTecnicoRepository.existsById(dto.getIdobsTecnico())) {
            throw new ResourceNotFoundException("ObsTecnico não encontrado com ID: " + dto.getIdobsTecnico());
        }
        domainValidationService.requireOrdemServico(dto.getOrdemServico());
        domainValidationService.requireFuncionario(dto.getFuncionario().getIdfuncionario());
        Obstecnico e = converterParaEntidade(dto);
        Obstecnico salvo = obsTecnicoRepository.save(e);
        log.info("ObsTecnico salvo com sucesso. ID: {}", salvo.getIdobsTecnico());
        return converterParaDTO(salvo);
    }

    @Transactional
    public void deletar(Integer id) {
        if (!obsTecnicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("ObsTecnico não encontrado com ID: " + id);
        }
        obsTecnicoRepository.deleteById(id);
        log.info("ObsTecnico deletado. ID: {}", id);
    }

    // Conversores simples (mapear apenas campos comuns)
    private ObsTecnicoDTO converterParaDTO(Obstecnico e) {
        if (e == null) return null;
        return ObsTecnicoDTO.builder()
                .idobsTecnico(e.getIdobsTecnico())
                .observacao(e.getObservacao())
                .datahora(e.getDatahora())
                .ativa(e.getAtiva())
                .funcionario(funcionarioService.converterParaDTO(e.getFuncionario()))
                .ordemServico(e.getOrdemServico())
                .build();
    }


    private Obstecnico converterParaEntidade(ObsTecnicoDTO dto) {
        Obstecnico e = new Obstecnico();
        e.setIdobsTecnico(dto.getIdobsTecnico());
        e.setObservacao(dto.getObservacao());
        e.setDatahora(dto.getDatahora());
        e.setAtiva(dto.getAtiva());
        e.setOrdemServico(dto.getOrdemServico());
        e.setFuncionario(domainValidationService.requireFuncionario(dto.getFuncionario().getIdfuncionario()));
        return e;
    }
}
