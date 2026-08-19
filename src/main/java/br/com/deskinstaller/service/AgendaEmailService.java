package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.AgendaEmailDTO;
import br.com.deskinstaller.exception.BusinessException;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.AgendaEmail;
import br.com.deskinstaller.repository.AgendaEmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Regras de negocio de AgendaEmail.
 *
 * <p>Por decisao de negocio nao existe exclusao: um endereco sai de circulacao
 * com {@code ativo = false} e pode ser reativado depois, preservando o historico.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AgendaEmailService {

    private final AgendaEmailRepository agendaEmailRepository;

    @Transactional(readOnly = true)
    public List<AgendaEmailDTO> listarTodos() {
        return agendaEmailRepository.findAllByOrderByEmailAsc().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AgendaEmailDTO> listarPorAtivo(Boolean ativo) {
        if (ativo == null) {
            return listarTodos();
        }
        return agendaEmailRepository.findByAtivoOrderByEmailAsc(ativo).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AgendaEmailDTO> buscarPorTrecho(String trecho) {
        if (trecho == null || trecho.isBlank()) {
            return listarTodos();
        }
        return agendaEmailRepository.findByEmailContainingIgnoreCaseOrderByEmailAsc(trecho.trim()).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<AgendaEmailDTO> buscarPorId(Integer id) {
        return agendaEmailRepository.findById(id).map(this::converterParaDTO);
    }

    /** Cria um novo endereco. Nasce ativo quando o campo nao e informado. */
    @Transactional
    public AgendaEmailDTO criar(AgendaEmailDTO dto) {
        String email = normalizar(dto.getEmail());
        garantirEmailUnico(email, null);

        AgendaEmail entidade = new AgendaEmail();
        entidade.setEmail(email);
        entidade.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : Boolean.TRUE);

        AgendaEmail salvo = agendaEmailRepository.save(entidade);
        log.info("AgendaEmail criado. ID: {}", salvo.getIdagendaemail());
        return converterParaDTO(salvo);
    }

    /**
     * Atualiza um endereco existente.
     *
     * <p>Campos ausentes no payload preservam o valor atual, para que uma
     * atualizacao parcial nunca apague dado por omissao.
     */
    @Transactional
    public AgendaEmailDTO atualizar(Integer id, AgendaEmailDTO dto) {
        AgendaEmail existente = exigir(id);

        String email = normalizar(dto.getEmail());
        if (email != null) {
            garantirEmailUnico(email, id);
            existente.setEmail(email);
        }
        if (dto.getAtivo() != null) {
            existente.setAtivo(dto.getAtivo());
        }

        AgendaEmail salvo = agendaEmailRepository.save(existente);
        log.info("AgendaEmail atualizado. ID: {}", salvo.getIdagendaemail());
        return converterParaDTO(salvo);
    }

    /** Tira o endereco de circulacao sem apagar o registro. */
    @Transactional
    public AgendaEmailDTO desativar(Integer id) {
        AgendaEmail existente = exigir(id);
        existente.setAtivo(Boolean.FALSE);
        log.info("AgendaEmail desativado. ID: {}", id);
        return converterParaDTO(agendaEmailRepository.save(existente));
    }

    @Transactional
    public AgendaEmailDTO ativar(Integer id) {
        AgendaEmail existente = exigir(id);
        existente.setAtivo(Boolean.TRUE);
        log.info("AgendaEmail ativado. ID: {}", id);
        return converterParaDTO(agendaEmailRepository.save(existente));
    }

    // ===== Apoio =====

    private AgendaEmail exigir(Integer id) {
        return agendaEmailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AgendaEmail não encontrado com ID: " + id));
    }

    private void garantirEmailUnico(String email, Integer idAtual) {
        if (email == null) {
            return;
        }
        agendaEmailRepository.findByEmailIgnoreCase(email)
                .filter(registro -> !registro.getIdagendaemail().equals(idAtual))
                .ifPresent(registro -> {
                    throw new BusinessException("Já existe um registro com o email informado");
                });
    }

    private String normalizar(String email) {
        if (email == null) {
            return null;
        }
        String limpo = email.trim();
        return limpo.isEmpty() ? null : limpo;
    }

    private AgendaEmailDTO converterParaDTO(AgendaEmail entidade) {
        return AgendaEmailDTO.builder()
                .idagendaemail(entidade.getIdagendaemail())
                .email(entidade.getEmail())
                .ativo(entidade.getAtivo())
                .build();
    }
}
