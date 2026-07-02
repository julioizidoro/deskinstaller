package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.FuncionarioDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.Funcionario;
import br.com.deskinstaller.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service para lógica de negócio relacionada a Funcionario
 * Implementa salvar (criar/atualizar), buscar e deletar
 *
 * @author Julio Izidoro
 * @since 2025-11-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;


    /**
     * Salva ou atualiza um Funcionario a partir do DTO
     */
    @Transactional
    public FuncionarioDTO salvar(FuncionarioDTO dto) {
        log.info("Salvando Funcionario: {}", dto.getNome());
        if (dto.getIdfuncionario() != null && !funcionarioRepository.existsById(dto.getIdfuncionario())) {
            throw new ResourceNotFoundException("Funcionario não encontrado com ID: " + dto.getIdfuncionario());
        }
        Funcionario funcionario = converterParaEntidade(dto);
        Funcionario salvo = funcionarioRepository.save(funcionario);
        log.info("Funcionario salvo com sucesso. ID: {}", salvo.getIdfuncionario());
        return converterParaDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<FuncionarioDTO> listarFuncionarios() {
        log.info("Listando Funcionarios");
        List<Funcionario> funcionarios = funcionarioRepository.findAll();
        return funcionarios.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    // método compatível com controller
    @Transactional(readOnly = true)
    public List<FuncionarioDTO> listarTodos() {
        return listarFuncionarios();
    }

    @Transactional(readOnly = true)
    public Optional<FuncionarioDTO> buscarPorId(Integer id) {
        return funcionarioRepository.findById(id).map(this::converterParaDTO);
    }

    @Transactional
    public void deletar(Integer id) {
        if (!funcionarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Funcionario não encontrado com ID: " + id);
        }
        funcionarioRepository.deleteById(id);
        log.info("Funcionario deletado. ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<FuncionarioDTO> listarPorAtivo(Boolean ativo) {
        if (ativo == null) {
            return listarTodos();
        }
        List<Funcionario> funcionarios = funcionarioRepository.findByAtivo(ativo);
        return funcionarios.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    // Conversores
    public FuncionarioDTO converterParaDTO(Funcionario e) {
        if (e == null) return null;
        return FuncionarioDTO.builder()
                .idfuncionario(e.getIdfuncionario())
                .nome(e.getNome())
                .foneCelular(e.getFoneCelular())
                .valorComissao(e.getValorComissao())
                .funcao(e.getFuncao())
                .ativo(e.isAtivo())
                .build();
    }

    public Funcionario converterParaEntidade(FuncionarioDTO dto) {
        Funcionario e = new Funcionario();
        e.setIdfuncionario(dto.getIdfuncionario());
        e.setNome(dto.getNome());
        e.setFoneCelular(dto.getFoneCelular());
        e.setValorComissao(dto.getValorComissao());
        e.setFuncao(dto.getFuncao());
        e.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : false);
        return e;
    }

}
