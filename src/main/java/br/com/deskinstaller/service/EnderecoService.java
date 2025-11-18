package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.EnderecoDTO;
import br.com.deskinstaller.model.Endereco;
import br.com.deskinstaller.repository.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service para lógica de negócio relacionada a Endereco
 * Implementa salvar (criar/atualizar), buscar e deletar
 *
 * @author Julio Izidoro
 * @since 2025-11-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    /**
     * Salva ou atualiza um Endereco a partir do DTO
     */
    @Transactional
    public EnderecoDTO salvar(EnderecoDTO dto) {
        log.info("Salvando Endereco: {}", dto.getLogradouro());
        Endereco Endereco = converterParaEntidade(dto);
        Endereco salvo = enderecoRepository.save(Endereco);
        log.info("Endereco salvo com sucesso. ID: {}", salvo.getIdendereco());
        return converterParaDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<EnderecoDTO> listarTodos() {
        log.info("Listando Enderecos");
        List<Endereco> Enderecos = enderecoRepository.findAll();
        return Enderecos.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<EnderecoDTO> buscarPorId(Integer id) {
        return enderecoRepository.findById(id).map(this::converterParaDTO);
    }

    @Transactional
    public void deletar(Integer id) {
        if (!enderecoRepository.existsById(id)) {
            throw new RuntimeException("Endereco não encontrado com ID: " + id);
        }
        enderecoRepository.deleteById(id);
        log.info("Endereco deletado. ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<EnderecoDTO> listarPorCliente(Integer clienteId) {
        System.out.println(clienteId);
        List<Endereco> enderecos = enderecoRepository.findByCliente(clienteId);
        System.out.println(enderecos);
        return enderecos.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    // Conversores
    private EnderecoDTO converterParaDTO(Endereco e) {
        if (e == null) return null;
        return EnderecoDTO.builder()
                .idendereco(e.getIdendereco())
                .tipoLogradouro(e.getTipoLogradouro())
                .logradouro(e.getLogradouro())
                .numero(e.getNumero())
                .complemento(e.getComplemento())
                .bairro(e.getBairro())
                .cep(e.getCep())
                .cidade(e.getCidade())
                .estado(e.getEstado())
                .pontoReferencia(e.getPontoReferencia())
                .idmaps(e.getIdmaps())
                .ativo(e.isAtivo())
                .cliente(e.getCliente())
                .build();
    }

    private Endereco converterParaEntidade(EnderecoDTO dto) {
        Endereco e = new Endereco();
        e.setIdendereco(dto.getIdendereco());
        e.setTipoLogradouro(dto.getTipoLogradouro());
        e.setLogradouro(dto.getLogradouro());
        e.setNumero(dto.getNumero());
        e.setComplemento(dto.getComplemento());
        e.setBairro(dto.getBairro());
        e.setCep(dto.getCep());
        e.setCidade(dto.getCidade());
        e.setEstado(dto.getEstado());
        e.setPontoReferencia(dto.getPontoReferencia());
        e.setIdmaps(dto.getIdmaps());
        e.setAtivo(dto.isAtivo());
        e.setCliente(dto.getCliente());
        return e;
    }

}
