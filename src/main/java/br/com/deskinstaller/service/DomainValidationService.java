package br.com.deskinstaller.service;

import br.com.deskinstaller.exception.BusinessException;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.Apcliente;
import br.com.deskinstaller.model.Cliente;
import br.com.deskinstaller.model.Endereco;
import br.com.deskinstaller.model.Funcionario;
import br.com.deskinstaller.model.Ordemservico;
import br.com.deskinstaller.model.Servico;
import br.com.deskinstaller.repository.ApClienteRepository;
import br.com.deskinstaller.repository.ClienteRepository;
import br.com.deskinstaller.repository.EnderecoRepository;
import br.com.deskinstaller.repository.FuncionarioRepository;
import br.com.deskinstaller.repository.OrdemServicoRepository;
import br.com.deskinstaller.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DomainValidationService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final ServicoRepository servicoRepository;
    private final ApClienteRepository apClienteRepository;

    @Transactional(readOnly = true)
    public Cliente requireCliente(Integer clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + clienteId));
    }

    @Transactional(readOnly = true)
    public Endereco requireEndereco(Integer enderecoId) {
        return enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereco não encontrado com ID: " + enderecoId));
    }

    @Transactional(readOnly = true)
    public Funcionario requireFuncionario(Integer funcionarioId) {
        return funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario não encontrado com ID: " + funcionarioId));
    }

    @Transactional(readOnly = true)
    public Ordemservico requireOrdemServico(Integer ordemServicoId) {
        return ordemServicoRepository.findByIdWithClienteAndEndereco(ordemServicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de serviço não encontrada com ID: " + ordemServicoId));
    }

    @Transactional(readOnly = true)
    public Servico requireServico(Integer servicoId) {
        return servicoRepository.findById(servicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Servico não encontrado com ID: " + servicoId));
    }

    @Transactional(readOnly = true)
    public Apcliente requireApcliente(Integer apclienteId) {
        return apClienteRepository.findById(apclienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Apcliente não encontrado com ID: " + apclienteId));
    }

    @Transactional(readOnly = true)
    public Endereco validateEnderecoDoCliente(Integer enderecoId, Integer clienteId) {
        Endereco endereco = requireEndereco(enderecoId);
        if (clienteId != null && !clienteId.equals(endereco.getCliente())) {
            throw new BusinessException("O endereço informado não pertence ao cliente selecionado");
        }
        return endereco;
    }

    @Transactional(readOnly = true)
    public Apcliente validateAparelhoDaOrdem(Integer apclienteId, Ordemservico ordemservico) {
        Apcliente apcliente = requireApcliente(apclienteId);

        if (ordemservico.getCliente() != null
                && !ordemservico.getCliente().getIdcliente().equals(apcliente.getCliente())) {
            throw new BusinessException("O aparelho informado não pertence ao cliente da ordem de serviço");
        }

        if (ordemservico.getEndereco() != null
                && apcliente.getEndereco() != null
                && !ordemservico.getEndereco().getIdendereco().equals(apcliente.getEndereco())) {
            throw new BusinessException("O aparelho informado não pertence ao endereço da ordem de serviço");
        }

        return apcliente;
    }
}
