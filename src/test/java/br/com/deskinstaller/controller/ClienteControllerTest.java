package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.ClienteDTO;
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
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.anyInt;
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

}

