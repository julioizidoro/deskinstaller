package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.FuncionarioDTO;
import br.com.deskinstaller.service.FuncionarioService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = FuncionarioController.class)
public class FuncionarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FuncionarioService funcionarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listar_retornaJson() throws Exception {
        FuncionarioDTO d1 = FuncionarioDTO.builder().idfuncionario(1).nome("A").build();
        FuncionarioDTO d2 = FuncionarioDTO.builder().idfuncionario(2).nome("B").build();
        List<FuncionarioDTO> list = Arrays.asList(d1, d2);
        when(funcionarioService.listarTodos()).thenReturn(list);

        mockMvc.perform(get("/api/funcionarios"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}

