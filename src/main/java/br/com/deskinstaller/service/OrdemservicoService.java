package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.OrdemServicoDTO;
import br.com.deskinstaller.exception.BusinessException;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.Cliente;
import br.com.deskinstaller.model.Endereco;
import br.com.deskinstaller.model.Ordemservico;
import br.com.deskinstaller.repository.ClienteRepository;
import br.com.deskinstaller.repository.EnderecoRepository;
import br.com.deskinstaller.repository.OrdemServicoRepository;
import br.com.deskinstaller.repository.OsFinanceiroRepository;
import br.com.deskinstaller.repository.OsFuncionarioRepository;
import br.com.deskinstaller.service.google.AgendaOrdemServicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Arrays;

/**
 * Service para lógica de negócio relacionada a Ordemservico
 *
 * @author Julio Izidoro
 * @since 2025-11-19
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrdemservicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final OsFuncionarioRepository osFuncionarioRepository;
    private final OsFinanceiroRepository osFinanceiroRepository;
    private final DomainValidationService domainValidationService;
    private final AgendaOrdemServicoService agendaOrdemServicoService;

    /**
     * Lista todas as ordens de serviço
     */
    @Transactional(readOnly = true)
    public List<OrdemServicoDTO> listarTodos() {
        log.info("Listando todas as ordens de serviço");
        List<Ordemservico> ordens = ordemServicoRepository.findAll();
        log.info("Total de ordens encontradas: {}", ordens.size());
        return ordens.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca ordem de serviço por ID
     */
    @Transactional(readOnly = true)
    public Optional<OrdemServicoDTO> buscarPorId(Integer id) {
        log.info("Buscando ordem de serviço por ID: {}", id);
        return ordemServicoRepository.findByIdWithCliente(id)
                .map(this::converterParaDTO);
    }

    /**
     * Lista ordens onde A) a situação NÃO é Cancelada/Finalizada OR
     * B) cuja datasituacao é nos últimos 7 dias (ou seja: traz ordens que
     * estejam abertas/ativas ou que tenham sido atualizadas nos últimos 7 dias).
     */
    @Transactional(readOnly = true)
    public List<OrdemServicoDTO> listarSituacaoNaoCanceladaOuFinalizadaUltimos7Dias() {
        List<String> situacoes = Arrays.asList("Cancelada", "Finalizada");
        // Limite: 7 dias anteriores à data atual (inclusive)
        Date limite = new Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000);

        // Busca usando OR com JOIN FETCH do cliente e endereco para evitar lazy loading
        List<Ordemservico> ordens = ordemServicoRepository.findBySituacaoNotInOrDatasituacaoGreaterThanEqualFetchClienteAndEndereco(situacoes, limite);

        return ordens.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Salva ou atualiza uma ordem de serviço
     */
    @Transactional
    public OrdemServicoDTO salvar(OrdemServicoDTO dto) {
        log.info("Salvando ordem de serviço: {}", dto.getIdordemServico());
        Ordemservico existente = null;
        if (dto.getIdordemServico() != null && !ordemServicoRepository.existsById(dto.getIdordemServico())) {
            throw new ResourceNotFoundException("Ordem de serviço não encontrada com ID: " + dto.getIdordemServico());
        }
        if (dto.getIdordemServico() != null) {
            existente = domainValidationService.requireOrdemServico(dto.getIdordemServico());
        }
        Ordemservico ordem = converterParaEntidade(dto);
        aplicarDefaultsEOuValidacoes(ordem, existente);
        Ordemservico salvo = ordemServicoRepository.save(ordem);
        log.info("Ordem de serviço salva com sucesso. ID: {}", salvo.getIdordemServico());
        return converterParaDTO(sincronizarAgenda(salvo));
    }

    @Transactional
    public OrdemServicoDTO finalizar(Integer id) {
        Ordemservico ordem = domainValidationService.requireOrdemServico(id);
        validarFinalizacao(ordem);
        ordem.setSituacao("Finalizada");
        ordem.setDatasituacao(new Date());
        return converterParaDTO(sincronizarAgenda(ordemServicoRepository.save(ordem)));
    }

    @Transactional
    public OrdemServicoDTO cancelar(Integer id) {
        Ordemservico ordem = domainValidationService.requireOrdemServico(id);
        if ("Finalizada".equalsIgnoreCase(ordem.getSituacao())) {
            throw new BusinessException("Não é permitido cancelar uma ordem de serviço já finalizada");
        }
        ordem.setSituacao("Cancelada");
        ordem.setDatasituacao(new Date());
        return converterParaDTO(sincronizarAgenda(ordemServicoRepository.save(ordem)));
    }

    /**
     * Deleta uma ordem de serviço por ID
     */
    @Transactional
    public void deletar(Integer id) {
        log.info("Deletando ordem de serviço ID: {}", id);
        if (!ordemServicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ordem de serviço não encontrada com ID: " + id);
        }
        ordemServicoRepository.findById(id).ifPresent(agendaOrdemServicoService::remover);
        ordemServicoRepository.deleteById(id);
        log.info("Ordem de serviço deletada com sucesso");
    }

    /**
     * Reflete a ordem na agenda da empresa e persiste o id do evento quando ele
     * muda. Falhas de integracao ja sao tratadas dentro do servico de agenda:
     * aqui elas nunca impedem a ordem de ser salva.
     */
    private Ordemservico sincronizarAgenda(Ordemservico ordem) {
        String eventoAnterior = ordem.getGoogleEventId();
        String eventoAtual = agendaOrdemServicoService.sincronizar(ordem);

        if (!java.util.Objects.equals(eventoAnterior, eventoAtual)) {
            ordem.setGoogleEventId(eventoAtual);
            return ordemServicoRepository.save(ordem);
        }
        return ordem;
    }

    // ===== Métodos de Conversão =====
    private OrdemServicoDTO converterParaDTO(Ordemservico ordem) {
        return OrdemServicoDTO.builder()
                .idordemServico(ordem.getIdordemServico())
                .horaServico(ordem.getHoraServico())
                .dataServico(ordem.getDataServico())
                .valor(ordem.getValor())
                .observacao(ordem.getObservacao())
                .situacao(ordem.getSituacao())
                .datasituacao(ordem.getDatasituacao())
                .valorComissao(ordem.getValorComissao())
                .clienteId(ordem.getCliente() != null ? ordem.getCliente().getIdcliente() : null)
                .clienteNome(ordem.getCliente() != null ? ordem.getCliente().getNome() : null)
                .enderecoId(ordem.getEndereco() != null ? ordem.getEndereco().getIdendereco() : null)
                .enderecoResumo(resumirEndereco(ordem.getEndereco()))
                .indicacao(ordem.getIndicacao())
                .recebida(ordem.isRecebida())
                .email(ordem.getEmail())
                .build();
    }

    private Ordemservico converterParaEntidade(OrdemServicoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + dto.getClienteId()));

        Endereco endereco = null;
        if (dto.getEnderecoId() != null) {
            endereco = domainValidationService.validateEnderecoDoCliente(dto.getEnderecoId(), dto.getClienteId());
        }

        Ordemservico ordem = new Ordemservico();
        ordem.setIdordemServico(dto.getIdordemServico());
        ordem.setHoraServico(dto.getHoraServico());
        ordem.setDataServico(dto.getDataServico());
        ordem.setValor(dto.getValor());
        ordem.setObservacao(dto.getObservacao());
        ordem.setSituacao(dto.getSituacao());
        ordem.setDatasituacao(dto.getDatasituacao());
        ordem.setValorComissao(dto.getValorComissao());
        ordem.setCliente(cliente);
        ordem.setEndereco(endereco);
        ordem.setIndicacao(dto.getIndicacao());
        ordem.setRecebida(dto.isRecebida());
        ordem.setEmail(dto.getEmail());

        // O DTO nao expoe o id do evento; sem isso, uma atualizacao apagaria o
        // vinculo com a agenda e criaria um evento duplicado na proxima sincronia.
        if (dto.getIdordemServico() != null) {
            ordemServicoRepository.findById(dto.getIdordemServico())
                    .map(Ordemservico::getGoogleEventId)
                    .ifPresent(ordem::setGoogleEventId);
        }
        return ordem;
    }

    private void aplicarDefaultsEOuValidacoes(Ordemservico ordem, Ordemservico existente) {
        if (ordem.getSituacao() == null || ordem.getSituacao().isBlank()) {
            ordem.setSituacao("Aberta");
        }
        if (ordem.getDatasituacao() == null) {
            ordem.setDatasituacao(new Date());
        }

        String situacao = ordem.getSituacao();
        if ("Finalizada".equalsIgnoreCase(situacao)) {
            if (ordem.getIdordemServico() == null) {
                throw new BusinessException("Uma ordem de serviço não pode ser criada já finalizada");
            }
            validarFinalizacao(existente != null ? existente : ordem);
            ordem.setDatasituacao(new Date());
        }

        if ("Cancelada".equalsIgnoreCase(situacao)) {
            if (existente != null && "Finalizada".equalsIgnoreCase(existente.getSituacao())) {
                throw new BusinessException("Não é permitido cancelar uma ordem de serviço já finalizada");
            }
            ordem.setDatasituacao(new Date());
        }
    }

    private void validarFinalizacao(Ordemservico ordem) {
        if (ordem.getIdordemServico() == null) {
            throw new BusinessException("A ordem de serviço precisa existir antes de ser finalizada");
        }

        long quantidadeFuncionarios = osFuncionarioRepository.findByOrdemServico(ordem.getIdordemServico()).size();
        if (quantidadeFuncionarios == 0) {
            throw new BusinessException("Não é possível finalizar a ordem de serviço sem equipe vinculada");
        }

        double totalRecebido = osFinanceiroRepository.findByOrdemservico(ordem.getIdordemServico()).stream()
                .map(item -> item.getValorrecebido() != null ? item.getValorrecebido().doubleValue() : 0.0)
                .reduce(0.0, Double::sum);

        if (totalRecebido < ordem.getValor()) {
            throw new BusinessException("Não é possível finalizar a ordem de serviço com valor recebido menor que o valor total");
        }
    }

    private String resumirEndereco(Endereco endereco) {
        if (endereco == null) {
            return null;
        }

        String logradouro = endereco.getLogradouro() != null ? endereco.getLogradouro() : "";
        String numero = endereco.getNumero() != null ? ", " + endereco.getNumero() : "";
        String cidade = endereco.getCidade() != null ? " - " + endereco.getCidade() : "";
        String resumo = (logradouro + numero + cidade).trim();
        return resumo.isEmpty() ? null : resumo;
    }
}
