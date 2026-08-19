package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.EnderecoDTO;
import br.com.deskinstaller.service.EnderecoService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EnderecoController.class)
public class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnderecoService enderecoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarPorCliente_retornaJson() throws Exception {
        EnderecoDTO d1 = EnderecoDTO.builder().idendereco(1).cliente(2).logradouro("A").build();
        EnderecoDTO d2 = EnderecoDTO.builder().idendereco(2).cliente(2).logradouro("B").build();
        List<EnderecoDTO> list = Arrays.asList(d1, d2);
        when(enderecoService.listarPorCliente(anyInt())).thenReturn(list);

        mockMvc.perform(get("/api/enderecos/cliente/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}

