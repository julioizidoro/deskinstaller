package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.OrdemServicoDTO;
import br.com.deskinstaller.model.Ordemservico;
import br.com.deskinstaller.repository.OrdemServicoRepository;
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
        return ordemServicoRepository.findById(id)
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
        Ordemservico ordem = converterParaEntidade(dto);
        Ordemservico salvo = ordemServicoRepository.save(ordem);
        log.info("Ordem de serviço salva com sucesso. ID: {}", salvo.getIdordemServico());
        return converterParaDTO(salvo);
    }

    /**
     * Deleta uma ordem de serviço por ID
     */
    @Transactional
    public void deletar(Integer id) {
        log.info("Deletando ordem de serviço ID: {}", id);
        if (!ordemServicoRepository.existsById(id)) {
            throw new RuntimeException("Ordem de serviço não encontrada com ID: " + id);
        }
        ordemServicoRepository.deleteById(id);
        log.info("Ordem de serviço deletada com sucesso");
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
                .cliente(ordem.getCliente())
                .endereco(ordem.getEndereco())
                .indicacao(ordem.getIndicacao())
                .recebida(ordem.isRecebida())
                .build();
    }

    private Ordemservico converterParaEntidade(OrdemServicoDTO dto) {
        Ordemservico ordem = new Ordemservico();
        ordem.setIdordemServico(dto.getIdordemServico());
        ordem.setHoraServico(dto.getHoraServico());
        ordem.setDataServico(dto.getDataServico());
        ordem.setValor(dto.getValor());
        ordem.setObservacao(dto.getObservacao());
        ordem.setSituacao(dto.getSituacao());
        ordem.setDatasituacao(dto.getDatasituacao());
        ordem.setValorComissao(dto.getValorComissao());
        ordem.setCliente(dto.getCliente());
        ordem.setEndereco(dto.getEndereco());
        ordem.setIndicacao(dto.getIndicacao());
        ordem.setRecebida(dto.isRecebida());
        return ordem;
    }
}
