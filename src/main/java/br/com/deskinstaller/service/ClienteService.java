package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.ClienteDTO;
import br.com.deskinstaller.exception.BusinessException;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.Cliente;
import br.com.deskinstaller.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service para lógica de negócio relacionada a Cliente
 *
 * @author Julio Izidoro
 * @since 2025-11-13
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService {

    private final ClienteRepository clienteRepository;

    /**
     * Lista todos os clientes
     */
    @Transactional(readOnly = true)
    public List<ClienteDTO> listarTodos() {
        log.info("Listando todos os clientes");
        List<Cliente> clientes = clienteRepository.findAll();
        log.info("Total de clientes encontrados: {}", clientes.size());
        return clientes.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca cliente por ID
     */
    @Transactional(readOnly = true)
    public Optional<ClienteDTO> buscarPorId(Integer id) {
        log.info("Buscando cliente por ID: {}", id);
        return clienteRepository.findById(id)
                .map(this::converterParaDTO);
    }

    /**
     * Busca clientes por nome (busca parcial)
     */
    @Transactional(readOnly = true)
    public List<ClienteDTO> buscarPorNome(String nome) {
        log.info("Buscando clientes por nome: {}", nome);
        List<Cliente> clientes = clienteRepository.findByNomeContainingIgnoreCase(nome);
        log.info("Encontrados {} clientes com nome contendo '{}'", clientes.size(), nome);
        return clientes.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca cliente por email
     */
    @Transactional(readOnly = true)
    public Optional<ClienteDTO> buscarPorEmail(String email) {
        log.info("Buscando cliente por email: {}", email);
        return clienteRepository.findByEmail(email)
                .map(this::converterParaDTO);
    }

    /**
     * Busca clientes por telefone
     */
    @Transactional(readOnly = true)
    public List<ClienteDTO> buscarPorTelefone(String telefone) {
        log.info("Buscando clientes por telefone: {}", telefone);
        List<Cliente> clientes = clienteRepository.findByFoneCelular(telefone);
        if (clientes.isEmpty()) {
            clientes = clienteRepository.findByFoneResidencial(telefone);
        }
        log.info("Encontrados {} clientes com telefone '{}'", clientes.size(), telefone);
        return clientes.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca clientes nascidos entre duas datas
     */
    @Transactional(readOnly = true)
    public List<ClienteDTO> buscarPorPeriodoNascimento(Date dataInicio, Date dataFim) {
        log.info("Buscando clientes nascidos entre {} e {}", dataInicio, dataFim);
        List<Cliente> clientes = clienteRepository.findByDataNascimentoBetween(dataInicio, dataFim);
        log.info("Encontrados {} clientes", clientes.size());
        return clientes.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca clientes com filtros múltiplos
     */
    @Transactional(readOnly = true)
    public List<ClienteDTO> buscarComFiltros(String nome, String email, String telefone) {
        log.info("Buscando clientes com filtros - Nome: {}, Email: {}, Telefone: {}",
                 nome, email, telefone);
        List<Cliente> clientes = clienteRepository.buscarComFiltros(nome, email, telefone);
        log.info("Encontrados {} clientes", clientes.size());
        return clientes.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Salva ou atualiza um cliente
     */
    @Transactional
    public ClienteDTO salvar(ClienteDTO clienteDTO) {
        log.info("Salvando cliente: {}", clienteDTO.getNome());

        if (clienteDTO.getIdcliente() == null && clienteDTO.getEmail() != null
                && !clienteDTO.getEmail().isBlank()
                && clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new BusinessException("Já existe cliente cadastrado com o email informado");
        }

        if (clienteDTO.getIdcliente() != null) {
            Cliente existente = clienteRepository.findById(clienteDTO.getIdcliente())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Cliente não encontrado com ID: " + clienteDTO.getIdcliente()));

            if (clienteDTO.getEmail() != null && !clienteDTO.getEmail().isBlank()) {
                clienteRepository.findByEmail(clienteDTO.getEmail())
                        .filter(cliente -> !cliente.getIdcliente().equals(existente.getIdcliente()))
                        .ifPresent(cliente -> {
                            throw new BusinessException("Já existe cliente cadastrado com o email informado");
                        });
            }
        }

        Cliente cliente = converterParaEntidade(clienteDTO);
        Cliente clienteSalvo = clienteRepository.save(cliente);
        log.info("Cliente salvo com sucesso. ID: {}", clienteSalvo.getIdcliente());

        return converterParaDTO(clienteSalvo);
    }

    /**
     * Deleta um cliente por ID
     */
    @Transactional
    public void deletar(Integer id) {
        log.info("Deletando cliente ID: {}", id);
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente não encontrado com ID: " + id);
        }
        clienteRepository.deleteById(id);
        log.info("Cliente deletado com sucesso");
    }

    /**
     * Conta total de clientes
     */
    @Transactional(readOnly = true)
    public long contarClientes() {
        long total = clienteRepository.count();
        log.info("Total de clientes no sistema: {}", total);
        return total;
    }

    /**
     * Verifica se cliente existe por email
     */
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    // ===== Métodos de Conversão =====

    /**
     * Converte entidade Cliente para DTO
     */
    /**
     * Conversao reaproveitada por outros services que devolvem o cliente
     * completo dentro dos seus proprios DTOs.
     */
    public ClienteDTO converterParaDTO(Cliente cliente) {
        return ClienteDTO.builder()
                .idcliente(cliente.getIdcliente())
                .nome(cliente.getNome())
                .tipoPessoa(cliente.getTipoPessoa())
                .dataNascimento(cliente.getDataNascimento())
                .foneResidencial(cliente.getFoneResidencial())
                .foneCelular(cliente.getFoneCelular())
                .foneComercial(cliente.getFoneComercial())
                .email(cliente.getEmail())
                .contato(cliente.getContato())
                .cpfcnpj(cliente.getCpfcnpj())
                .rgie(cliente.getRgie())
                .build();
    }

    /**
     * Converte DTO para entidade Cliente
     */
    private Cliente converterParaEntidade(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setIdcliente(dto.getIdcliente());
        cliente.setNome(dto.getNome());
        cliente.setTipoPessoa(dto.getTipoPessoa());
        cliente.setDataNascimento(dto.getDataNascimento());
        cliente.setFoneResidencial(dto.getFoneResidencial());
        cliente.setFoneCelular(dto.getFoneCelular());
        cliente.setFoneComercial(dto.getFoneComercial());
        cliente.setEmail(dto.getEmail());
        cliente.setContato(dto.getContato());
        cliente.setCpfcnpj(dto.getCpfcnpj());
        cliente.setRgie(dto.getRgie());
        return cliente;
    }
}
