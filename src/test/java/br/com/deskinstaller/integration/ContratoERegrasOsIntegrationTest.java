package br.com.deskinstaller.integration;

import br.com.deskinstaller.dto.ClienteDTO;
import br.com.deskinstaller.dto.EnderecoDTO;
import br.com.deskinstaller.dto.FuncionarioDTO;
import br.com.deskinstaller.dto.OrdemServicoDTO;
import br.com.deskinstaller.dto.OsFinanceiroDTO;
import br.com.deskinstaller.dto.OsFuncionarioDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContratoERegrasOsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contratoLegadoOsFinanceiro_deveFuncionar() throws Exception {
        OrdemServicoDTO ordem = criarOrdemBasica();

        String financeiroResponse = mockMvc.perform(post("/api/osfinanceiro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OsFinanceiroDTO.builder()
                                .ordemservico(ordem.getIdordemServico())
                                .data(LocalDate.now())
                                .parcelas(1)
                                .valorrecebido(50f)
                                .formapagamento("PIX")
                                .build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idosfinanceiro").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OsFinanceiroDTO financeiro = objectMapper.readValue(financeiroResponse, OsFinanceiroDTO.class);

        mockMvc.perform(get("/api/osfinanceiro/os/{id}", ordem.getIdordemServico()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ordemservico").value(ordem.getIdordemServico()));

        mockMvc.perform(delete("/api/osfinanceiro/deletar/{id}", financeiro.getIdosfinanceiro()))
                .andExpect(status().isNoContent());
    }

    @Test
    void criarOrdemComEnderecoDeOutroCliente_deveRetornar400() throws Exception {
        ClienteDTO cliente1 = criarCliente("cliente1@example.com");
        ClienteDTO cliente2 = criarCliente("cliente2@example.com");
        EnderecoDTO enderecoCliente2 = criarEndereco(cliente2.getIdcliente(), "Rua B");

        mockMvc.perform(post("/api/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrdemServicoDTO.builder()
                                .clienteId(cliente1.getIdcliente())
                                .enderecoId(enderecoCliente2.getIdendereco())
                                .horaServico("09:00")
                                .dataServico(new Date())
                                .valor(100.0)
                                .situacao("Aberta")
                                .recebida(false)
                                .build())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O endereço informado não pertence ao cliente selecionado"));
    }

    @Test
    void finalizarOrdemSemEquipeOuFinanceiro_deveRetornar400() throws Exception {
        OrdemServicoDTO ordem = criarOrdemBasica();

        mockMvc.perform(patch("/api/ordens-servico/{id}/finalizar", ordem.getIdordemServico()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Não é possível finalizar a ordem de serviço sem equipe vinculada"));
    }

    @Test
    void finalizarOrdemComEquipeEFinanceiro_deveFuncionar() throws Exception {
        OrdemServicoDTO ordem = criarOrdemBasica();
        FuncionarioDTO funcionario = criarFuncionario();

        mockMvc.perform(post("/api/os/funcionario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OsFuncionarioDTO.builder()
                                .ordemServico(ordem.getIdordemServico())
                                .funcionario(FuncionarioDTO.builder()
                                        .idfuncionario(funcionario.getIdfuncionario())
                                        .build())
                                .build())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/osfinanceiro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OsFinanceiroDTO.builder()
                                .ordemservico(ordem.getIdordemServico())
                                .data(LocalDate.now())
                                .parcelas(1)
                                .valorrecebido(150f)
                                .formapagamento("Dinheiro")
                                .build())))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/ordens-servico/{id}/finalizar", ordem.getIdordemServico()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("Finalizada"));
    }

    @Test
    void vincularFuncionarioInexistenteNaOs_deveRetornar404() throws Exception {
        OrdemServicoDTO ordem = criarOrdemBasica();

        mockMvc.perform(post("/api/os/funcionario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OsFuncionarioDTO.builder()
                                .ordemServico(ordem.getIdordemServico())
                                .funcionario(FuncionarioDTO.builder()
                                        .idfuncionario(999999)
                                        .build())
                                .build())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Funcionario não encontrado com ID: 999999"));
    }

    private OrdemServicoDTO criarOrdemBasica() throws Exception {
        ClienteDTO cliente = criarCliente("ordem@example.com");
        EnderecoDTO endereco = criarEndereco(cliente.getIdcliente(), "Rua Principal");

        String response = mockMvc.perform(post("/api/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrdemServicoDTO.builder()
                                .clienteId(cliente.getIdcliente())
                                .enderecoId(endereco.getIdendereco())
                                .horaServico("08:30")
                                .dataServico(new Date())
                                .valor(150.0)
                                .situacao("Aberta")
                                .recebida(false)
                                .build())))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, OrdemServicoDTO.class);
    }

    private ClienteDTO criarCliente(String email) throws Exception {
        String response = mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ClienteDTO.builder()
                                .nome("Cliente " + email)
                                .tipoPessoa("F")
                                .dataNascimento(new Date())
                                .email(email)
                                .foneCelular("11999999999")
                                .build())))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, ClienteDTO.class);
    }

    private EnderecoDTO criarEndereco(Integer clienteId, String logradouro) throws Exception {
        String response = mockMvc.perform(post("/api/enderecos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(EnderecoDTO.builder()
                                .cliente(clienteId)
                                .logradouro(logradouro)
                                .numero("100")
                                .bairro("Centro")
                                .cidade("Sao Paulo")
                                .estado("SP")
                                .build())))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, EnderecoDTO.class);
    }

    private FuncionarioDTO criarFuncionario() throws Exception {
        String response = mockMvc.perform(post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(FuncionarioDTO.builder()
                                .nome("Tecnico 1")
                                .foneCelular("11988887777")
                                .funcao("Tecnico")
                                .ativo(true)
                                .build())))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, FuncionarioDTO.class);
    }
}
