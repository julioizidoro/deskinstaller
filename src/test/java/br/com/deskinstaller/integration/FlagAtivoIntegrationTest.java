package br.com.deskinstaller.integration;

import br.com.deskinstaller.dto.ApclienteDTO;
import br.com.deskinstaller.dto.ClienteDTO;
import br.com.deskinstaller.dto.EnderecoDTO;
import br.com.deskinstaller.dto.FuncionarioDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cobre a regra de que a flag {@code ativo} nao pode ser apagada por um payload
 * que simplesmente nao envia o campo, e que campos existentes na entidade nao
 * podem ser perdidos numa atualizacao.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FlagAtivoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void criarEnderecoSemAtivo_deveNascerAtivo() throws Exception {
        ClienteDTO cliente = criarCliente("endereco.ativo@example.com");

        mockMvc.perform(post("/api/enderecos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(EnderecoDTO.builder()
                                .logradouro("Rua Sem Ativo")
                                .numero("10")
                                .bairro("Centro")
                                .cidade("Sao Paulo")
                                .estado("SP")
                                .cliente(cliente.getIdcliente())
                                .build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void atualizarEnderecoSemAtivo_devePreservarValorAtual() throws Exception {
        ClienteDTO cliente = criarCliente("endereco.preserva@example.com");

        String criado = mockMvc.perform(post("/api/enderecos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(EnderecoDTO.builder()
                                .logradouro("Rua Original")
                                .numero("20")
                                .bairro("Centro")
                                .cidade("Sao Paulo")
                                .estado("SP")
                                .foneInstalacao("1133334444")
                                .ativo(true)
                                .cliente(cliente.getIdcliente())
                                .build())))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EnderecoDTO endereco = objectMapper.readValue(criado, EnderecoDTO.class);

        // Payload de atualizacao sem o campo "ativo": o endereco deve continuar ativo.
        mockMvc.perform(put("/api/enderecos/{id}", endereco.getIdendereco())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(EnderecoDTO.builder()
                                .idendereco(endereco.getIdendereco())
                                .logradouro("Rua Atualizada")
                                .numero("20")
                                .bairro("Centro")
                                .cidade("Sao Paulo")
                                .estado("SP")
                                .foneInstalacao("1133334444")
                                .cliente(cliente.getIdcliente())
                                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Rua Atualizada"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void enderecoDevePersistirFoneInstalacao() throws Exception {
        ClienteDTO cliente = criarCliente("endereco.fone@example.com");

        mockMvc.perform(post("/api/enderecos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(EnderecoDTO.builder()
                                .logradouro("Rua Com Fone")
                                .numero("30")
                                .bairro("Centro")
                                .cidade("Sao Paulo")
                                .estado("SP")
                                .foneInstalacao("1199998888")
                                .cliente(cliente.getIdcliente())
                                .build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.foneInstalacao").value("1199998888"));
    }

    @Test
    void criarFuncionarioSemAtivo_deveNascerAtivo() throws Exception {
        mockMvc.perform(post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(FuncionarioDTO.builder()
                                .nome("Tecnico Sem Flag")
                                .foneCelular("11977776666")
                                .funcao("Tecnico")
                                .build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void criarAparelhoSemAtivo_deveNascerAtivo() throws Exception {
        ClienteDTO cliente = criarCliente("aparelho.ativo@example.com");

        mockMvc.perform(post("/api/aparelhos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ApclienteDTO.builder()
                                .cliente(cliente.getIdcliente())
                                .modelo("Split 9000")
                                .fabricante("ACME")
                                .dataCompra(new Date())
                                .build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ativo").value(true));
    }

    private ClienteDTO criarCliente(String email) throws Exception {
        String response = mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ClienteDTO.builder()
                                .nome("Cliente Flag Ativo")
                                .tipoPessoa("F")
                                .dataNascimento(new Date())
                                .email(email)
                                .build())))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, ClienteDTO.class);
    }
}
