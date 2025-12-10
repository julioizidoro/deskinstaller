package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.OsFinanceiroDTO;
import br.com.deskinstaller.model.OsFinanceiro;
import br.com.deskinstaller.repository.OsFinanceiroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service para lógica de negócio relacionada a OsFinanceiro
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class OsFinanceiroService {

    private final OsFinanceiroRepository osFinanceiroRepository;


    @Transactional(readOnly = true)
    public List<OsFinanceiroDTO> listarOS(Integer idOrdemServico) {
        List<OsFinanceiro> list = osFinanceiroRepository.findByOrdemservico(idOrdemServico);
        System.out.println(list);
        return list.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<OsFinanceiroDTO> buscarPorId(Integer id) {
        return osFinanceiroRepository.findById(id).map(this::converterParaDTO);
    }

    @Transactional
    public OsFinanceiroDTO salvar(OsFinanceiroDTO dto) {
        log.info("Salvando OsFinanceiro: {}", dto);
        OsFinanceiro e = converterParaEntidade(dto);
        OsFinanceiro salvo = osFinanceiroRepository.save(e);
        log.info("OsFinanceiro salvo com sucesso. ID: {}", salvo.getIdosfinanceiro());
        return converterParaDTO(salvo);
    }

    @Transactional
    public void deletar(Integer id) {
        if (!osFinanceiroRepository.existsById(id)) {
            throw new RuntimeException("OsFinanceiro não encontrado com ID: " + id);
        }
        osFinanceiroRepository.deleteById(id);
        log.info("RelServico deletado. ID: {}", id);
    }


    // Conversores simples (mapear apenas campos comuns)
    private OsFinanceiroDTO converterParaDTO(OsFinanceiro e) {
        if (e == null) return null;
        return OsFinanceiroDTO.builder()
                .idosfinanceiro(e.getIdosfinanceiro())
                .data(e.getData())
                .parcelas(e.getParcelas())
                .ordemservico(e.getOrdemservico())
                .valorrecebido(e.getValorrecebido())
                .formapagamento(e.getFormapagamento())
                .build();
    }


    private OsFinanceiro converterParaEntidade(OsFinanceiroDTO dto) {
        OsFinanceiro e = new OsFinanceiro();
        e.setIdosfinanceiro(dto.getIdosfinanceiro());
        e.setData(dto.getData());
        e.setParcelas(dto.getParcelas());
        e.setOrdemservico(dto.getOrdemservico());
        e.setValordesconto(e.getValordesconto());
        e.setValorrecebido(e.getValorrecebido());
        e.setFormapagamento(dto.getFormapagamento());
        return e;
    }


}
