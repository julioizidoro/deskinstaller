package br.com.deskinstaller.integration;

import br.com.deskinstaller.dto.ClienteDTO;
import br.com.deskinstaller.model.Cliente;
import br.com.deskinstaller.repository.ClienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClienteApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void fluxoCrudDeCliente_funcionaDePontaAPonta() throws Exception {
        ClienteDTO novoCliente = ClienteDTO.builder()
                .nome("Maria Silva")
                .tipoPessoa("F")
                .dataNascimento(new Date())
                .email("maria.silva@example.com")
                .foneCelular("11999999999")
                .rgie("1234567")
                .build();

        String response = mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoCliente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idcliente").exists())
                .andExpect(jsonPath("$.rgie").value("1234567"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ClienteDTO criado = objectMapper.readValue(response, ClienteDTO.class);

        mockMvc.perform(get("/api/clientes/{id}", criado.getIdcliente()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.rgie").value("1234567"));

        criado.setNome("Maria Silva Atualizada");
        criado.setEmail("maria.atualizada@example.com");

        mockMvc.perform(put("/api/clientes/{id}", criado.getIdcliente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Silva Atualizada"))
                .andExpect(jsonPath("$.email").value("maria.atualizada@example.com"));

        mockMvc.perform(delete("/api/clientes/{id}", criado.getIdcliente()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/clientes/{id}", criado.getIdcliente()))
                .andExpect(status().isNotFound());
    }

    @Test
    void criarCliente_comEmailDuplicado_retorna400() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Base");
        cliente.setTipoPessoa("F");
        cliente.setDataNascimento(new Date());
        cliente.setEmail("duplicado@example.com");
        clienteRepository.save(cliente);

        ClienteDTO novoCliente = ClienteDTO.builder()
                .nome("Outro Cliente")
                .tipoPessoa("F")
                .dataNascimento(new Date())
                .email("duplicado@example.com")
                .build();

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoCliente)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Já existe cliente cadastrado com o email informado"));
    }
}
