package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.OsFuncionarioDTO;
import br.com.deskinstaller.dto.FuncionarioDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.OsFuncionario;
import br.com.deskinstaller.model.Funcionario;
import br.com.deskinstaller.repository.OsFuncionarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para lógica de negócio relacionada a OsFuncionario
 *
 * @author Julio Izidoro
 * @since 2025-11-19
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OsFuncionarioService {

    private final OsFuncionarioRepository osFuncionarioRepository;
    private final DomainValidationService domainValidationService;

    /**
     * Busca funcionários por id da ordem de serviço
     */
    @Transactional(readOnly = true)
    public List<OsFuncionarioDTO> buscarPorIdOrdemServico(Integer id) {
        log.info("Buscando funcionários da ordem de serviço por ID: {}", id);
        return osFuncionarioRepository.findByOrdemServico(id).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Salva ou atualiza um registro OsFuncionario
     */
    @Transactional
    public OsFuncionarioDTO salvar(OsFuncionarioDTO dto) {
        log.info("Salvando OsFuncionario: {}", dto.getIdosFuncionario());
        if (dto.getIdosFuncionario() != null && !osFuncionarioRepository.existsById(dto.getIdosFuncionario())) {
            throw new ResourceNotFoundException("OsFuncionario não encontrado com ID: " + dto.getIdosFuncionario());
        }
        domainValidationService.requireOrdemServico(dto.getOrdemServico());
        domainValidationService.requireFuncionario(dto.getFuncionario().getIdfuncionario());
        OsFuncionario entidade = converterParaEntidade(dto);
        OsFuncionario salvo = osFuncionarioRepository.save(entidade);
        log.info("OsFuncionario salvo com sucesso. ID: {}", salvo.getIdosFuncionario());
        return converterParaDTO(salvo);
    }

    /**
     * Deleta um registro OsFuncionario por ID
     */
    @Transactional
    public void deletar(Integer id) {
        log.info("Deletando OsFuncionario ID: {}", id);
        if (!osFuncionarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("OsFuncionario não encontrado com ID: " + id);
        }
        osFuncionarioRepository.deleteById(id);
        log.info("OsFuncionario deletado com sucesso");
    }

    // ===== Métodos de Conversão =====
    private OsFuncionarioDTO converterParaDTO(OsFuncionario entidade) {
        FuncionarioDTO funcionarioDTO = null;
        if (entidade.getFuncionario() != null) {
            Funcionario f = entidade.getFuncionario();
            funcionarioDTO = FuncionarioDTO.builder()
                    .idfuncionario(f.getIdfuncionario())
                    .nome(f.getNome())
                    .foneCelular(f.getFoneCelular())
                    .valorComissao(f.getValorComissao())
                    .funcao(f.getFuncao())
                    .ativo(f.isAtivo())
                    .build();
        }

        return OsFuncionarioDTO.builder()
                .idosFuncionario(entidade.getIdosFuncionario())
                .ordemServico(entidade.getOrdemServico())
                .funcionario(funcionarioDTO)
                .build();
    }

    private OsFuncionario converterParaEntidade(OsFuncionarioDTO dto) {
        OsFuncionario entidade = new OsFuncionario();
        entidade.setIdosFuncionario(dto.getIdosFuncionario());
        entidade.setOrdemServico(dto.getOrdemServico());
        if (dto.getFuncionario() != null) {
            entidade.setFuncionario(domainValidationService.requireFuncionario(dto.getFuncionario().getIdfuncionario()));
        }
        return entidade;
    }
}
