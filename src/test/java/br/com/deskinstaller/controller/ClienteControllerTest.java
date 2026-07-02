package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.ClienteDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ClienteController.class)
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listar_retornaJson() throws Exception {
        ClienteDTO d1 = ClienteDTO.builder().idcliente(1).nome("A").build();
        ClienteDTO d2 = ClienteDTO.builder().idcliente(2).nome("B").build();
        List<ClienteDTO> list = Arrays.asList(d1, d2);
        when(clienteService.listarTodos()).thenReturn(list);

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void buscarPorId_quandoNaoEncontrado_retorna404Padronizado() throws Exception {
        when(clienteService.buscarPorId(99)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente não encontrado com ID: 99"));
    }

    @Test
    void criar_quandoValido_retorna201() throws Exception {
        ClienteDTO entrada = ClienteDTO.builder()
                .nome("Cliente Teste")
                .tipoPessoa("F")
                .dataNascimento(new Date())
                .email("cliente@example.com")
                .build();
        ClienteDTO salvo = ClienteDTO.builder()
                .idcliente(10)
                .nome(entrada.getNome())
                .tipoPessoa(entrada.getTipoPessoa())
                .dataNascimento(entrada.getDataNascimento())
                .email(entrada.getEmail())
                .build();
        when(clienteService.salvar(any(ClienteDTO.class))).thenReturn(salvo);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idcliente").value(10));
    }

    @Test
    void criar_quandoInvalido_retorna400() throws Exception {
        ClienteDTO entrada = ClienteDTO.builder().build();

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"));
    }

}
