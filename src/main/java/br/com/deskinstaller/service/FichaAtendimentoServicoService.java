package br.com.deskinstaller.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.deskinstaller.dto.FichaAtendimentoServicoDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.Fichaatendimento;
import br.com.deskinstaller.model.Fichaatendimentoservico;
import br.com.deskinstaller.repository.FichaAtendimentoRepository;
import br.com.deskinstaller.repository.FichaAtendimentoServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Regras de negocio dos itens de servico da ficha de atendimento.
 *
 * @author Julio Izidoro
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FichaAtendimentoServicoService {

    private final FichaAtendimentoServicoRepository fichaAtendimentoServicoRepository;
    private final FichaAtendimentoRepository fichaAtendimentoRepository;

    @Transactional(readOnly = true)
    public List<FichaAtendimentoServicoDTO> listarPorFicha(Integer idfichaatendimento) {
        return fichaAtendimentoServicoRepository
                .findByFichaatendimentoIdfichaatendimento(idfichaatendimento).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<FichaAtendimentoServicoDTO> buscarPorId(Integer id) {
        return fichaAtendimentoServicoRepository.findById(id).map(this::converterParaDTO);
    }

    @Transactional
    public FichaAtendimentoServicoDTO salvar(FichaAtendimentoServicoDTO dto) {
        if (dto.getFichaatendimento() == null) {
            throw new IllegalArgumentException("fichaatendimento é obrigatório");
        }
        Fichaatendimento ficha = fichaAtendimentoRepository.findById(dto.getFichaatendimento())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ficha de atendimento não encontrada com ID: " + dto.getFichaatendimento()));

        Fichaatendimentoservico item;
        if (dto.getIdfichaatendimentosdrvico() != null) {
            item = fichaAtendimentoServicoRepository.findById(dto.getIdfichaatendimentosdrvico())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Item de serviço não encontrado com ID: " + dto.getIdfichaatendimentosdrvico()));
        } else {
            item = new Fichaatendimentoservico();
        }
        item.setQuantidade(dto.getQuantidade());
        item.setDescricao(dto.getDescricao());
        item.setFichaatendimento(ficha);

        Fichaatendimentoservico salvo = fichaAtendimentoServicoRepository.save(item);
        log.info("Item de serviço salvo com sucesso. ID: {}", salvo.getIdfichaatendimentosdrvico());
        return converterParaDTO(salvo);
    }

    @Transactional
    public void deletar(Integer id) {
        if (!fichaAtendimentoServicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item de serviço não encontrado com ID: " + id);
        }
        fichaAtendimentoServicoRepository.deleteById(id);
        log.info("Item de serviço deletado. ID: {}", id);
    }

    public FichaAtendimentoServicoDTO converterParaDTO(Fichaatendimentoservico e) {
        if (e == null) {
            return null;
        }
        return FichaAtendimentoServicoDTO.builder()
                .idfichaatendimentosdrvico(e.getIdfichaatendimentosdrvico())
                .quantidade(e.getQuantidade())
                .descricao(e.getDescricao())
                .fichaatendimento(e.getFichaatendimento() != null
                        ? e.getFichaatendimento().getIdfichaatendimento()
                        : null)
                .build();
    }
}
