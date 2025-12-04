package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.RelServicoDTO;
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
        log.info("Salvando RelServico: {}", dto);
        Relservico e = converterParaEntidade(dto);
        Relservico salvo = relServicoRepository.save(e);
        log.info("RelServico salvo com sucesso. ID: {}", salvo.getIdrelServico());
        return converterParaDTO(salvo);
    }

    @Transactional
    public void deletar(Integer id) {
        if (!relServicoRepository.existsById(id)) {
            throw new RuntimeException("RelServico não encontrado com ID: " + id);
        }
        relServicoRepository.deleteById(id);
        log.info("RelServico deletado. ID: {}", id);
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
                .build();
    }


    private Relservico converterParaEntidade(RelServicoDTO dto) {
        Relservico e = new Relservico();
        e.setIdrelServico(dto.getIdrelServico());
        e.setDescricao(dto.getDescricao());
        e.setValor(dto.getValor());
        e.setQuantidade(dto.getQuantidade());
        e.setOrdemservico(dto.getOrdemservico());
        e.setServico(servicoService.converterParaEntidade(dto.getServico()));
        e.setApCliente(apClienteService.converterParaEntidade(dto.getApCliente()));
        return e;
    }


}
