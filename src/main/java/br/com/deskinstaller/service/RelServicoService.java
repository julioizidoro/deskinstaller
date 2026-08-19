package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.RelServicoDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.Relservico;
import br.com.deskinstaller.repository.RelServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service para lógica de negócio relacionada a RelServico
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class RelServicoService {

    private final RelServicoRepository relServicoRepository;
    private final ServicoService servicoService;
    private final ApClienteService apClienteService;
    private final DomainValidationService domainValidationService;

    @Transactional(readOnly = true)
    public List<RelServicoDTO> listarOS(Integer idOrdemServico) {
        List<Relservico> list = relServicoRepository.findByOrdemservico(idOrdemServico);
        return list.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<RelServicoDTO> buscarPorId(Integer id) {
        return relServicoRepository.findById(id).map(this::converterParaDTO);
    }

    @Transactional
    public RelServicoDTO salvar(RelServicoDTO dto) {
        log.info("Salvando relação de serviço para ordem {}", dto.getOrdemservico());
        if (dto.getIdrelServico() != null && !relServicoRepository.existsById(dto.getIdrelServico())) {
            throw new ResourceNotFoundException("Relação de serviço não encontrada com ID: " + dto.getIdrelServico());
        }
        domainValidationService.requireOrdemServico(dto.getOrdemservico());
        domainValidationService.requireServico(dto.getServico().getIdservico());
        domainValidationService.validateAparelhoDaOrdem(dto.getApCliente().getIdapCliente(),
                domainValidationService.requireOrdemServico(dto.getOrdemservico()));
        Relservico e = converterParaEntidade(dto);
        Relservico salvo = relServicoRepository.save(e);
        log.info("Relação de serviço salva com sucesso. ID: {}", salvo.getIdrelServico());
        return converterParaDTO(salvo);
    }

    @Transactional
    public void deletar(Integer id) {
        if (!relServicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Relação de serviço não encontrada com ID: " + id);
        }
        relServicoRepository.deleteById(id);
        log.info("Relação de serviço deletada. ID: {}", id);
    }


    // Conversores simples (mapear apenas campos comuns)
    private RelServicoDTO converterParaDTO(Relservico e) {
        if (e == null) return null;
        return RelServicoDTO.builder()
                .idrelServico(e.getIdrelServico())
                .descricao(e.getDescricao())
                .valor(e.getValor())
                .quantidade(e.getQuantidade())
                .ordemservico(e.getOrdemservico())
                .servico(servicoService.converterParaDTO(e.getServico()))
                .apCliente(apClienteService.converterParaDTO(e.getApCliente()))
                .situacao(e.isSituacao())
                .build();
    }


    private Relservico converterParaEntidade(RelServicoDTO dto) {
        var ordem = domainValidationService.requireOrdemServico(dto.getOrdemservico());
        var servico = domainValidationService.requireServico(dto.getServico().getIdservico());
        var apcliente = domainValidationService.validateAparelhoDaOrdem(dto.getApCliente().getIdapCliente(), ordem);

        Relservico e = new Relservico();
        e.setIdrelServico(dto.getIdrelServico());
        e.setDescricao(dto.getDescricao());
        e.setValor(dto.getValor());
        e.setQuantidade(dto.getQuantidade());
        e.setOrdemservico(dto.getOrdemservico());
        e.setServico(servico);
        e.setApCliente(apcliente);
        e.setSituacao(dto.isSituacao());
        return e;
    }


}
