package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.OsDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.*;
import br.com.deskinstaller.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para preparar dados de Ordem de Serviço para visualização HTML/PDF.
 * Busca dados do banco e converte para OsDTO usado pelo template Thymeleaf.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OsPDFService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final RelServicoRepository relServicoRepository;
    private final ApClienteRepository apClienteRepository;
    private final OsFuncionarioRepository osFuncionarioRepository;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Busca ordem de serviço do banco e converte para DTO para visualização HTML.
     *
     * @param idOrdemServico ID da ordem de serviço
     * @return OsDTO preenchido com dados do banco
     * @throws ResourceNotFoundException se a OS não for encontrada
     */
    @Transactional(readOnly = true)
    public OsDTO buscarOsParaVisualizacao(Integer idOrdemServico) {
        log.info("Buscando dados da OS {} para visualização HTML/PDF", idOrdemServico);

        // 1. Buscar ordem de serviço
        Ordemservico os = ordemServicoRepository.findByIdWithCliente(idOrdemServico)
                .orElseThrow(() -> {
                    log.warn("Ordem de Serviço não encontrada: {}", idOrdemServico);
                    return new ResourceNotFoundException("Ordem de Serviço não encontrada: " + idOrdemServico);
                });

        // 2. Buscar dados relacionados (já vêm como objetos do relacionamento JPA)
        Cliente cliente = os.getCliente();
        Endereco endereco = os.getEndereco();
        List<Relservico> servicos = relServicoRepository.findByOrdemservico(idOrdemServico);
        List<OsFuncionario> listaOsFuncionario = osFuncionarioRepository.findByOrdemServico(idOrdemServico);

        // Log de diagnóstico para entender problemas de 404/500
        log.debug("OS encontrada: id={} ; clienteId={} ; enderecoId={}",
                os.getIdordemServico(),
                cliente != null && cliente.getIdcliente() != null ? cliente.getIdcliente() : "null",
                endereco != null && endereco.getIdendereco() != null ? endereco.getIdendereco() : "null");

        log.debug("Serviços encontrados: {} ; Funcionários da OS: {}",
                servicos != null ? servicos.size() : 0,
                listaOsFuncionario != null ? listaOsFuncionario.size() : 0);

        // 3. Converter para DTO
        return converterParaOsDTO(os, cliente, endereco, servicos, listaOsFuncionario);
    }

    /**
     * Converte entidades JPA para OsDTO usado pelo template Thymeleaf.
     */
    private OsDTO converterParaOsDTO(Ordemservico os, Cliente cliente, Endereco endereco,
                                      List<Relservico> servicos, List<OsFuncionario> listaOsFuncionario) {

        // Formatar técnicos: pegar nome e último sobrenome de cada um
        String tecnicos = listaOsFuncionario != null && !listaOsFuncionario.isEmpty()
                ? listaOsFuncionario.stream()
                    .map(osFunc -> extrairNomeESobrenome(osFunc.getFuncionario() != null ? osFunc.getFuncionario().getNome() : null))
                    .collect(Collectors.joining(" - "))
                : "-";

        return OsDTO.builder()
                // Cabeçalho
                .numero(os.getIdordemServico() != null ? os.getIdordemServico().toString() : "-")

                // Dados do Cliente
                .endereco(formatarEndereco(endereco))
                .bairro(endereco != null ? endereco.getBairro() : "-")
                .cidade(endereco != null ? endereco.getCidade() : "-")
                .clienteCodigo(cliente != null && cliente.getIdcliente() != null
                        ? cliente.getIdcliente().toString() : "-")
                .clienteNome(cliente != null ? cliente.getNome() : "-")
                .clienteCnpj(cliente != null ? formatarCpfCnpj(cliente.getCpfcnpj()) : "-")
                .foneCel(cliente != null ? cliente.getFoneCelular() : "-")

                // Dados do Serviço
                .data(os.getDataServico() != null ? dateFormat.format(os.getDataServico()) : "-")
                .hora(os.getHoraServico() != null ? os.getHoraServico() : "-")
                .tecnico(tecnicos)

                // Lista de serviços
                .servicos(servicos.stream()
                        .map(this::converterServicoDTO)
                        .collect(Collectors.toList()))

                // Total
                .total(formatarValor(os.getValor()))
                .build();
    }

    /**
     * Converte Relservico para ServicoDTO.
     */
    private OsDTO.ServicoDTO converterServicoDTO(Relservico relservico) {
        // Buscar dados do aparelho (se houver)
        Apcliente aparelho = null;
        if (relservico.getApCliente() != null) {
            aparelho = apClienteRepository.findById(relservico.getApCliente().getIdapCliente())
                    .orElse(null);
        }

        // Montar descrição do serviço: nome do serviço + descrição adicional
        StringBuilder descricaoServico = new StringBuilder();
        if (relservico.getServico() != null && relservico.getServico().getDescricao() != null) {
            descricaoServico.append(relservico.getServico().getDescricao());
        }
        if (relservico.getDescricao() != null && !relservico.getDescricao().trim().isEmpty()) {
            if (descricaoServico.length() > 0) {
                descricaoServico.append(": ");
            }
            descricaoServico.append(relservico.getDescricao());
        }

        // Montar descrição do aparelho: Local - Fabricante - Modelo - Capacidade
        String descricaoAparelho = "";
        if (aparelho != null) {
            StringBuilder sb = new StringBuilder();
            if (aparelho.getLocal() != null && !aparelho.getLocal().trim().isEmpty()) {
                sb.append(aparelho.getLocal());
            }
            if (aparelho.getFabricante() != null && !aparelho.getFabricante().trim().isEmpty()) {
                if (sb.length() > 0) sb.append(" - ");
                sb.append(aparelho.getFabricante());
            }
            if (aparelho.getModelo() != null && !aparelho.getModelo().trim().isEmpty()) {
                if (sb.length() > 0) sb.append(" - ");
                sb.append(aparelho.getModelo());
            }
            if (aparelho.getCapacidade() != null && !aparelho.getCapacidade().trim().isEmpty()) {
                if (sb.length() > 0) sb.append(" - ");
                sb.append(aparelho.getCapacidade());
            }
            descricaoAparelho = sb.length() > 0 ? sb.toString() : "";
        }

        return OsDTO.ServicoDTO.builder()
                .descricao(descricaoServico.length() > 0 ? descricaoServico.toString() : "-")
                .aparelho(descricaoAparelho.isEmpty() ? "-" : descricaoAparelho)
                .quantidade(formatarQuantidade(relservico.getQuantidade()))
                .valor(formatarValor(relservico.getValor()))
                .build();
    }



    /**
     * Formata endereço completo.
     */
    private String formatarEndereco(Endereco endereco) {
        if (endereco == null) {
            return "-";
        }

        StringBuilder sb = new StringBuilder();
        if (endereco.getTipoLogradouro() != null && !endereco.getTipoLogradouro().trim().isEmpty()) {
            sb.append(endereco.getTipoLogradouro()).append(" ");
        }
        if (endereco.getLogradouro() != null) {
            sb.append(endereco.getLogradouro());
        }
        if (endereco.getNumero() != null && !endereco.getNumero().trim().isEmpty()) {
            sb.append(", ").append(endereco.getNumero());
        }
        if (endereco.getComplemento() != null && !endereco.getComplemento().trim().isEmpty()) {
            sb.append(" - ").append(endereco.getComplemento());
        }

        return !sb.isEmpty() ? sb.toString() : "-";
    }

    /**
     * Extrai nome e último sobrenome de um nome completo.
     * Exemplo: "Carlos Eduardo Goes" → "Carlos Goes"
     */
    private String extrairNomeESobrenome(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.trim().isEmpty()) {
            return "-";
        }

        String[] partes = nomeCompleto.trim().split("\\s+");

        if (partes.length == 1) {
            // Apenas um nome
            return partes[0];
        } else if (partes.length == 2) {
            // Nome e sobrenome
            return nomeCompleto.trim();
        } else {
            // Nome completo: pega primeiro e último
            return partes[0] + " " + partes[partes.length - 1];
        }
    }

    /**
     * Formata CPF/CNPJ (adiciona máscara se necessário).
     */
    private String formatarCpfCnpj(String cpfCnpj) {
        if (cpfCnpj == null || cpfCnpj.trim().isEmpty()) {
            return "-";
        }

        // Remove caracteres não numéricos
        String numeros = cpfCnpj.replaceAll("[^0-9]", "");

        // Se já está formatado, retorna como está
        if (cpfCnpj.contains(".") || cpfCnpj.contains("/") || cpfCnpj.contains("-")) {
            return cpfCnpj;
        }

        // Formata CPF (11 dígitos)
        if (numeros.length() == 11) {
            return String.format("%s.%s.%s-%s",
                    numeros.substring(0, 3),
                    numeros.substring(3, 6),
                    numeros.substring(6, 9),
                    numeros.substring(9, 11));
        }

        // Formata CNPJ (14 dígitos)
        if (numeros.length() == 14) {
            return String.format("%s.%s.%s/%s-%s",
                    numeros.substring(0, 2),
                    numeros.substring(2, 5),
                    numeros.substring(5, 8),
                    numeros.substring(8, 12),
                    numeros.substring(12, 14));
        }

        // Retorna original se não for CPF nem CNPJ
        return cpfCnpj;
    }

    /**
     * Formata valor monetário (double para String com 2 decimais).
     */
    private String formatarValor(double valor) {
        return String.format("%.2f", valor).replace(".", ",");
    }

    /**
     * Formata quantidade (double para String com 3 decimais).
     */
    private String formatarQuantidade(double quantidade) {
        return String.format("%.3f", quantidade).replace(".", ",");
    }
}
