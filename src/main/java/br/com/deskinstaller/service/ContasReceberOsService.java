package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.ContasReceberOsDTO;
import br.com.deskinstaller.exception.BusinessException;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.Contasreceberos;
import br.com.deskinstaller.repository.ContasReceberOsRepository;
import br.com.deskinstaller.repository.ContasReceberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service para lógica de negócio do vínculo entre título a receber e OS.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContasReceberOsService {

    private final ContasReceberOsRepository contasReceberOsRepository;
    private final ContasReceberRepository contasReceberRepository;
    private final DomainValidationService domainValidationService;

    @Transactional(readOnly = true)
    public List<ContasReceberOsDTO> listarPorContasReceber(Integer idContasReceber) {
        return contasReceberOsRepository.findByContasreceberidcontasreceber(idContasReceber).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContasReceberOsDTO> listarPorOrdemServico(Integer idOrdemServico) {
        return contasReceberOsRepository.findByOrdemservicoidordemServico(idOrdemServico).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ContasReceberOsDTO> buscarPorId(Integer id) {
        return contasReceberOsRepository.findById(id).map(this::converterParaDTO);
    }

    @Transactional
    public ContasReceberOsDTO salvar(ContasReceberOsDTO dto) {
        log.info("Salvando ContasReceberOs: {}", dto.getIdcontasreceberos());
        if (dto.getIdcontasreceberos() != null
                && !contasReceberOsRepository.existsById(dto.getIdcontasreceberos())) {
            throw new ResourceNotFoundException(
                    "ContasReceberOs não encontrado com ID: " + dto.getIdcontasreceberos());
        }

        if (!contasReceberRepository.existsById(dto.getContasreceberidcontasreceber())) {
            throw new ResourceNotFoundException(
                    "ContasReceber não encontrado com ID: " + dto.getContasreceberidcontasreceber());
        }
        domainValidationService.requireOrdemServico(dto.getOrdemservicoidordemServico());

        // Em inclusao, impede vincular duas vezes o mesmo titulo a mesma OS.
        if (dto.getIdcontasreceberos() == null
                && contasReceberOsRepository.existsByContasreceberidcontasreceberAndOrdemservicoidordemServico(
                        dto.getContasreceberidcontasreceber(), dto.getOrdemservicoidordemServico())) {
            throw new BusinessException("Esta ordem de serviço já está vinculada a este título a receber");
        }

        Contasreceberos salvo = contasReceberOsRepository.save(converterParaEntidade(dto));
        log.info("ContasReceberOs salvo com sucesso. ID: {}", salvo.getIdcontasreceberos());
        return converterParaDTO(salvo);
    }

    @Transactional
    public void deletar(Integer id) {
        if (!contasReceberOsRepository.existsById(id)) {
            throw new ResourceNotFoundException("ContasReceberOs não encontrado com ID: " + id);
        }
        contasReceberOsRepository.deleteById(id);
        log.info("ContasReceberOs deletado. ID: {}", id);
    }

    private ContasReceberOsDTO converterParaDTO(Contasreceberos e) {
        if (e == null) {
            return null;
        }
        return ContasReceberOsDTO.builder()
                .idcontasreceberos(e.getIdcontasreceberos())
                .contasreceberidcontasreceber(e.getContasreceberidcontasreceber())
                .ordemservicoidordemServico(e.getOrdemservicoidordemServico())
                .build();
    }

    private Contasreceberos converterParaEntidade(ContasReceberOsDTO dto) {
        Contasreceberos e = new Contasreceberos();
        e.setIdcontasreceberos(dto.getIdcontasreceberos());
        e.setContasreceberidcontasreceber(dto.getContasreceberidcontasreceber());
        e.setOrdemservicoidordemServico(dto.getOrdemservicoidordemServico());
        return e;
    }
}
