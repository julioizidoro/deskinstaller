package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.ApclienteDTO;
import br.com.deskinstaller.service.ApClienteService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ApClienteController.class)
public class ApClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApClienteService apClienteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listar_retornaListaJson() throws Exception {
        ApclienteDTO d1 = ApclienteDTO.builder().idapCliente(1).cliente(2).dataCompra(new Date()).build();
        ApclienteDTO d2 = ApclienteDTO.builder().idapCliente(2).cliente(2).dataCompra(new Date()).build();
        List<ApclienteDTO> list = Arrays.asList(d1, d2);
        when(apClienteService.listarPorCliente(anyInt())).thenReturn(list);

        mockMvc.perform(get("/api/aparelhos?clienteId=2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}

