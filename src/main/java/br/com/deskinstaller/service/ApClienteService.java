package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.ApclienteDTO;
import br.com.deskinstaller.model.Apcliente;
import br.com.deskinstaller.repository.ApClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service para lógica de negócio relacionada a Apcliente
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApClienteService {

    private final ApClienteRepository apClienteRepository;

    @Transactional
    public ApclienteDTO salvar(ApclienteDTO dto) {
        log.info("Salvando Apcliente: {}", dto);
        Apcliente e = converterParaEntidade(dto);
        Apcliente salvo = apClienteRepository.save(e);
        log.info("Apcliente salvo com sucesso. ID: {}", salvo.getIdapCliente());
        return converterParaDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<ApclienteDTO> listarTodos() {
        List<Apcliente> list = apClienteRepository.findAll();
        return list.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApclienteDTO> listarPorCliente(Integer clienteId) {
        if (clienteId == null) {
            return Collections.emptyList();
        }
        List<Apcliente> list = apClienteRepository.findByCliente(clienteId);
        return list.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApclienteDTO> listarPorClienteEndereco(Integer clienteId, Integer enderecoId) {
        if (clienteId == null || enderecoId == null) {
            return Collections.emptyList();
        }
        List<Apcliente> list = apClienteRepository.findByClienteAndEndereco(clienteId, enderecoId);
        return list.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ApclienteDTO> buscarPorId(Integer id) {
        return apClienteRepository.findById(id).map(this::converterParaDTO);
    }

    @Transactional
    public void deletar(Integer id) {
        if (!apClienteRepository.existsById(id)) {
            throw new RuntimeException("Apcliente não encontrado com ID: " + id);
        }
        apClienteRepository.deleteById(id);
        log.info("Apcliente deletado. ID: {}", id);
    }

    // Conversores simples (mapear apenas campos comuns)
    public ApclienteDTO converterParaDTO(Apcliente e) {
        if (e == null) return null;
        return ApclienteDTO.builder()
                .idapCliente(e.getIdapCliente())
                .dataCompra(e.getDataCompra())
                .notaFiscal(e.getNotaFiscal())
                .loja(e.getLoja())
                .dataInstalacao(e.getDataInstalacao())
                .dataManutencao(e.getDataManutencao())
                .local(e.getLocal())
                .cliente(e.getCliente())
                .endereco(e.getEndereco())
                .modelo(e.getModelo())
                .fabricante(e.getFabricante())
                .modeloEvaporadora(e.getModeloEvaporadora())
                .nsEvaporadora(e.getNsEvaporadora())
                .modeloCodensadora(e.getModeloCodensadora())
                .nsCodensadora(e.getNsCodensadora())
                .capacidade(e.getCapacidade())
                .dataultimamanutencao(e.getDataultimamanutencao())
                .ativo(e.isAtivo())
                .build();
    }

    public Apcliente converterParaEntidade(ApclienteDTO dto) {
        Apcliente e = new Apcliente();
        e.setIdapCliente(dto.getIdapCliente());
        e.setDataCompra(dto.getDataCompra());
        e.setNotaFiscal(dto.getNotaFiscal());
        e.setLoja(dto.getLoja());
        e.setDataInstalacao(dto.getDataInstalacao());
        e.setDataManutencao(dto.getDataManutencao());
        e.setLocal(dto.getLocal());
        e.setCliente(dto.getCliente());
        e.setEndereco(dto.getEndereco());
        e.setModelo(dto.getModelo());
        e.setFabricante(dto.getFabricante());
        e.setModeloEvaporadora(dto.getModeloEvaporadora());
        e.setNsEvaporadora(dto.getNsEvaporadora());
        e.setModeloCodensadora(dto.getModeloCodensadora());
        e.setNsCodensadora(dto.getNsCodensadora());
        e.setCapacidade(dto.getCapacidade());
        e.setDataultimamanutencao(dto.getDataultimamanutencao());
        e.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : false);
        return e;
    }

}
