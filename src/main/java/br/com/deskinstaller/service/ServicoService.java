package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.ServicoDTO;
import br.com.deskinstaller.model.Servico;
import br.com.deskinstaller.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service para lógica de negócio relacionada a Servico
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicoService {

    private final ServicoRepository servicoRepository;

    @Transactional(readOnly = true)
    public List<ServicoDTO> listarTodos() {
        List<Servico> list = servicoRepository.findAll();
        return list.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ServicoDTO> buscarPorId(Integer id) {
        return servicoRepository.findById(id).map(this::converterParaDTO);
    }






    // Conversores simples (mapear apenas campos comuns)
    public ServicoDTO converterParaDTO(Servico e) {
        if (e == null) return null;
        return ServicoDTO.builder()
                .idservico(e.getIdservico())
                .descricao(e.getDescricao())
                .build();
    }

    public Servico converterParaEntidade(ServicoDTO dto) {
        if (dto == null) return null;
        Servico e = new Servico();
        e.setIdservico(dto.getIdservico());
        e.setDescricao(dto.getDescricao());
        return e;
    }


}
