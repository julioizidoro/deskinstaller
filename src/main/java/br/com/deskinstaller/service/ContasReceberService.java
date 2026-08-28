package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.ContasReceberDTO;
import br.com.deskinstaller.exception.BusinessException;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.Cliente;
import br.com.deskinstaller.model.Contasreceber;
import br.com.deskinstaller.repository.ContasReceberOsRepository;
import br.com.deskinstaller.repository.ContasReceberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service para lógica de negócio relacionada a Contasreceber.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContasReceberService {

    private final ContasReceberRepository contasReceberRepository;
    private final ContasReceberOsRepository contasReceberOsRepository;
    private final DomainValidationService domainValidationService;
    private final ClienteService clienteService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    @Transactional(readOnly = true)
    public List<ContasReceberDTO> listarTodos() {
        return contasReceberRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ContasReceberDTO> buscarPorId(Integer id) {
        return contasReceberRepository.findById(id).map(this::converterParaDTO);
    }

    @Transactional(readOnly = true)
    public List<ContasReceberDTO> listarPorVencimento(LocalDate inicio, LocalDate fim) {
        return contasReceberRepository
                .findByDatavencimentoBetweenOrderByDatavencimentoAsc(inicio, fim).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContasReceberDTO> listarPorUsuario(Integer usuarioidusuario) {
        return contasReceberRepository.findByUsuarioidusuario(usuarioidusuario).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContasReceberDTO> listarPorCliente(Integer clienteidcliente) {
        return contasReceberRepository.findByCliente_IdclienteOrderByDatavencimentoAsc(clienteidcliente).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Títulos ainda em aberto: nada recebido e sem data de recebimento.
     */
    @Transactional(readOnly = true)
    public List<ContasReceberDTO> listarEmAberto() {
        return contasReceberRepository.findEmAberto().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContasReceberDTO salvar(ContasReceberDTO dto) {
        log.info("Salvando ContasReceber: {}", dto.getIdcontasreceber());
        if (dto.getIdcontasreceber() != null && !contasReceberRepository.existsById(dto.getIdcontasreceber())) {
            throw new ResourceNotFoundException("ContasReceber não encontrado com ID: " + dto.getIdcontasreceber());
        }
        dto.setUsuarioidusuario(resolverUsuario(dto));
        domainValidationService.requireUsuario(dto.getUsuarioidusuario());

        Contasreceber salvo = contasReceberRepository.save(converterParaEntidade(dto));
        log.info("ContasReceber salvo com sucesso. ID: {}", salvo.getIdcontasreceber());
        return converterParaDTO(salvo);
    }

    /**
     * Remove o título e, antes, os vínculos com ordens de serviço — a FK de
     * contasreceberos impediria a exclusão direta.
     */
    @Transactional
    public void deletar(Integer id) {
        if (!contasReceberRepository.existsById(id)) {
            throw new ResourceNotFoundException("ContasReceber não encontrado com ID: " + id);
        }
        contasReceberOsRepository.deleteByContasreceberidcontasreceber(id);
        contasReceberRepository.deleteById(id);
        log.info("ContasReceber deletado. ID: {}", id);
    }

    /**
     * Define o dono do titulo no servidor, ignorando o que o cliente enviou.
     *
     * - Na alteracao, preserva o usuario que registrou o titulo originalmente.
     * - Na inclusao, usa o usuario autenticado (token JWT).
     * - Sem autenticacao (app.security.enabled=false), cai para o valor do DTO.
     */
    private Integer resolverUsuario(ContasReceberDTO dto) {
        if (dto.getIdcontasreceber() != null) {
            Integer original = contasReceberRepository.findById(dto.getIdcontasreceber())
                    .map(Contasreceber::getUsuarioidusuario)
                    .orElse(null);
            if (original != null) {
                return original;
            }
        }
        Integer enviado = (dto.getUsuarioidusuario() != null && dto.getUsuarioidusuario() > 0)
                ? dto.getUsuarioidusuario() : null;
        Integer id = usuarioAutenticadoService.idUsuarioAtual().orElse(enviado);
        if (id == null) {
            throw new BusinessException("Nao foi possivel identificar o usuario autenticado para o titulo.");
        }
        return id;
    }

    private ContasReceberDTO converterParaDTO(Contasreceber e) {
        if (e == null) {
            return null;
        }
        return ContasReceberDTO.builder()
                .idcontasreceber(e.getIdcontasreceber())
                .dataemissao(e.getDataemissao())
                .numero(e.getNumero())
                .valorreceber(e.getValorreceber())
                .datavencimento(e.getDatavencimento())
                .valorrecebido(e.getValorrecebido())
                .datarecebimento(e.getDatarecebimento())
                .valorjuros(e.getValorjuros())
                .valordesconto(e.getValordesconto())
                .observacao(e.getObservacao())
                .numeronf(e.getNumeronf())
                .usuarioidusuario(e.getUsuarioidusuario())
                .clienteidcliente(e.getCliente() != null ? e.getCliente().getIdcliente() : null)
                .cliente(e.getCliente() != null ? clienteService.converterParaDTO(e.getCliente()) : null)
                .build();
    }

    private Contasreceber converterParaEntidade(ContasReceberDTO dto) {
        Cliente cliente = domainValidationService.requireCliente(dto.getClienteidcliente());

        Contasreceber e = new Contasreceber();
        e.setIdcontasreceber(dto.getIdcontasreceber());
        e.setDataemissao(dto.getDataemissao());
        e.setNumero(dto.getNumero());
        e.setValorreceber(dto.getValorreceber());
        e.setDatavencimento(dto.getDatavencimento());
        e.setValorrecebido(dto.getValorrecebido());
        e.setDatarecebimento(dto.getDatarecebimento());
        e.setValorjuros(dto.getValorjuros());
        e.setValordesconto(dto.getValordesconto());
        e.setObservacao(dto.getObservacao());
        e.setNumeronf(dto.getNumeronf());
        e.setUsuarioidusuario(dto.getUsuarioidusuario());
        e.setCliente(cliente);
        return e;
    }
}
