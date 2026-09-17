package br.com.deskinstaller.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.deskinstaller.dto.FichaAtendimentoDTO;
import br.com.deskinstaller.dto.FichaAtendimentoServicoDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.Cliente;
import br.com.deskinstaller.model.Endereco;
import br.com.deskinstaller.model.Fichaatendimento;
import br.com.deskinstaller.model.Fichaatendimentoservico;
import br.com.deskinstaller.model.Funcionario;
import br.com.deskinstaller.repository.FichaAtendimentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Regras de negocio da Ficha de Atendimento.
 *
 * @author Julio Izidoro
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FichaAtendimentoService {

    private final FichaAtendimentoRepository fichaAtendimentoRepository;
    private final DomainValidationService domainValidationService;
    private final ClienteService clienteService;
    private final EnderecoService enderecoService;
    private final FuncionarioService funcionarioService;

    @Transactional(readOnly = true)
    public List<FichaAtendimentoDTO> listarTodos() {
        return fichaAtendimentoRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<FichaAtendimentoDTO> buscarPorId(Integer id) {
        return fichaAtendimentoRepository.findById(id).map(this::converterParaDTO);
    }

    @Transactional(readOnly = true)
    public List<FichaAtendimentoDTO> listarPorCliente(Integer idcliente) {
        return fichaAtendimentoRepository.findByClienteIdclienteOrderByDatavisitaDesc(idcliente).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FichaAtendimentoDTO> listarPorEndereco(Integer idendereco) {
        return fichaAtendimentoRepository.findByEnderecoIdenderecoOrderByDatavisitaDesc(idendereco).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FichaAtendimentoDTO> listarPorFuncionario(Integer idfuncionario) {
        return fichaAtendimentoRepository.findByFuncionarioIdfuncionarioOrderByDatavisitaDesc(idfuncionario).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FichaAtendimentoDTO> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return fichaAtendimentoRepository.findByDatavisitaBetweenOrderByDatavisitaDesc(inicio, fim).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public FichaAtendimentoDTO salvar(FichaAtendimentoDTO dto) {
        log.info("Salvando ficha de atendimento do cliente {}", dto.getCliente());

        Fichaatendimento entidade;
        if (dto.getIdfichaatendimento() != null) {
            entidade = fichaAtendimentoRepository.findById(dto.getIdfichaatendimento())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Ficha de atendimento não encontrada com ID: " + dto.getIdfichaatendimento()));
        } else {
            entidade = new Fichaatendimento();
        }

        Funcionario funcionario = domainValidationService.requireFuncionario(dto.getFuncionario());
        Cliente cliente = domainValidationService.requireCliente(dto.getCliente());
        // Garante que o endereco informado pertence ao cliente da ficha.
        Endereco endereco = domainValidationService.validateEnderecoDoCliente(dto.getEndereco(), dto.getCliente());

        entidade.setDatavisita(dto.getDatavisita());
        entidade.setHoravisita(dto.getHoravisita());
        entidade.setSituacao(dto.getSituacao());
        entidade.setObservacao(dto.getObservacao());
        entidade.setFuncionario(funcionario);
        entidade.setCliente(cliente);
        entidade.setEndereco(endereco);

        // Substitui os itens preservando orphanRemoval (nunca trocar a instancia da lista).
        if (entidade.getServicos() == null) {
            entidade.setServicos(new ArrayList<>());
        }
        entidade.getServicos().clear();
        if (dto.getServicos() != null) {
            for (FichaAtendimentoServicoDTO itemDTO : dto.getServicos()) {
                Fichaatendimentoservico item = new Fichaatendimentoservico();
                item.setIdfichaatendimentosdrvico(itemDTO.getIdfichaatendimentosdrvico());
                item.setQuantidade(itemDTO.getQuantidade());
                item.setDescricao(itemDTO.getDescricao());
                entidade.addServico(item);
            }
        }

        Fichaatendimento salvo = fichaAtendimentoRepository.save(entidade);
        log.info("Ficha de atendimento salva com sucesso. ID: {}", salvo.getIdfichaatendimento());
        return converterParaDTO(salvo);
    }

    @Transactional
    public void deletar(Integer id) {
        if (!fichaAtendimentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ficha de atendimento não encontrada com ID: " + id);
        }
        fichaAtendimentoRepository.deleteById(id);
        log.info("Ficha de atendimento deletada. ID: {}", id);
    }

    public FichaAtendimentoDTO converterParaDTO(Fichaatendimento e) {
        if (e == null) {
            return null;
        }
        List<FichaAtendimentoServicoDTO> itens = e.getServicos() == null
                ? new ArrayList<>()
                : e.getServicos().stream()
                        .map(i -> FichaAtendimentoServicoDTO.builder()
                                .idfichaatendimentosdrvico(i.getIdfichaatendimentosdrvico())
                                .quantidade(i.getQuantidade())
                                .descricao(i.getDescricao())
                                .fichaatendimento(e.getIdfichaatendimento())
                                .build())
                        .collect(Collectors.toList());

        return FichaAtendimentoDTO.builder()
                .idfichaatendimento(e.getIdfichaatendimento())
                .datavisita(e.getDatavisita())
                .horavisita(e.getHoravisita())
                .situacao(e.getSituacao())
                .observacao(e.getObservacao())
                .funcionario(e.getFuncionario() != null ? e.getFuncionario().getIdfuncionario() : null)
                .cliente(e.getCliente() != null ? e.getCliente().getIdcliente() : null)
                .endereco(e.getEndereco() != null ? e.getEndereco().getIdendereco() : null)
                .funcionarioDados(funcionarioService.converterParaDTO(e.getFuncionario()))
                .clienteDados(e.getCliente() != null ? clienteService.converterParaDTO(e.getCliente()) : null)
                .enderecoDados(enderecoService.converterParaDTO(e.getEndereco()))
                .servicos(itens)
                .build();
    }
}
