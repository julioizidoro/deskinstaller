package br.com.deskinstaller.integration;

import br.com.deskinstaller.model.Apcliente;
import br.com.deskinstaller.repository.ApClienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApClienteApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApClienteRepository apClienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarPorClienteEndereco_respeitaOsDoisFiltros() throws Exception {
        apClienteRepository.save(criarAparelho(1, 10, "Sala"));
        apClienteRepository.save(criarAparelho(1, 20, "Quarto"));
        apClienteRepository.save(criarAparelho(2, 10, "Escritorio"));

        mockMvc.perform(get("/api/aparelhos/cliente/1/endereco/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].local").value("Sala"));
    }

    private Apcliente criarAparelho(int cliente, int endereco, String local) {
        Apcliente aparelho = new Apcliente();
        aparelho.setCliente(cliente);
        aparelho.setEndereco(endereco);
        aparelho.setLocal(local);
        aparelho.setModelo("Split");
        aparelho.setAtivo(true);
        return aparelho;
    }
}
