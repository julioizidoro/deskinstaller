package br.com.deskinstaller.integration;

import br.com.deskinstaller.dto.ApclienteDTO;
import br.com.deskinstaller.dto.ClienteDTO;
import br.com.deskinstaller.dto.EnderecoDTO;
import br.com.deskinstaller.dto.OrdemServicoDTO;
import br.com.deskinstaller.dto.RelServicoDTO;
import br.com.deskinstaller.dto.ServicoDTO;
import br.com.deskinstaller.model.Servico;
import br.com.deskinstaller.repository.ServicoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FluxoOrdemServicoPdfIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServicoRepository servicoRepository;

    @Test
    void deveGerarPdfNoFluxoCompletoDaOrdemDeServico() throws Exception {
        ClienteDTO clienteCriado = objectMapper.readValue(
                mockMvc.perform(post("/api/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(ClienteDTO.builder()
                                        .nome("Cliente Fluxo Completo")
                                        .tipoPessoa("F")
                                        .dataNascimento(new Date())
                                        .email("fluxo.completo@example.com")
                                        .foneCelular("11988887777")
                                        .build())))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                ClienteDTO.class
        );

        EnderecoDTO enderecoCriado = objectMapper.readValue(
                mockMvc.perform(post("/api/enderecos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(EnderecoDTO.builder()
                                        .cliente(clienteCriado.getIdcliente())
                                        .logradouro("Rua das Flores")
                                        .numero("123")
                                        .bairro("Centro")
                                        .cidade("Sao Paulo")
                                        .estado("SP")
                                        .build())))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                EnderecoDTO.class
        );

        ApclienteDTO aparelhoCriado = objectMapper.readValue(
                mockMvc.perform(post("/api/aparelhos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(ApclienteDTO.builder()
                                        .cliente(clienteCriado.getIdcliente())
                                        .endereco(enderecoCriado.getIdendereco())
                                        .local("Sala")
                                        .modelo("Split 12000")
                                        .fabricante("ACME")
                                        .capacidade("12000 BTUs")
                                        .dataCompra(new Date())
                                        .build())))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                ApclienteDTO.class
        );

        OrdemServicoDTO ordemCriada = objectMapper.readValue(
                mockMvc.perform(post("/api/ordens-servico")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(OrdemServicoDTO.builder()
                                        .clienteId(clienteCriado.getIdcliente())
                                        .enderecoId(enderecoCriado.getIdendereco())
                                        .horaServico("08:30")
                                        .dataServico(new Date())
                                        .datasituacao(new Date())
                                        .situacao("ABERTA")
                                        .valor(350.0)
                                        .observacao("Instalacao inicial")
                                        .recebida(false)
                                        .build())))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                OrdemServicoDTO.class
        );

        Servico servico = new Servico();
        servico.setDescricao("Instalacao");
        servico.setSituacao(true);
        Servico servicoSalvo = servicoRepository.save(servico);

        mockMvc.perform(post("/api/rel-servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RelServicoDTO.builder()
                                .ordemservico(ordemCriada.getIdordemServico())
                                .descricao("Instalacao do aparelho da sala")
                                .quantidade(1)
                                .valor(350.0)
                                .situacao(true)
                                .servico(ServicoDTO.builder()
                                        .idservico(servicoSalvo.getIdservico())
                                        .descricao(servicoSalvo.getDescricao())
                                        .situacao(true)
                                        .build())
                                .apCliente(aparelhoCriado)
                                .build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idrelServico").exists());

        mockMvc.perform(get("/api/rel-servicos/os/{id}", ordemCriada.getIdordemServico()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ordemservico").value(ordemCriada.getIdordemServico()))
                .andExpect(jsonPath("$[0].apCliente.idapCliente").value(aparelhoCriado.getIdapCliente()));

        mockMvc.perform(get("/api/ordens-servico/{id}/pdf", ordemCriada.getIdordemServico()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"OS-" + ordemCriada.getIdordemServico() + ".pdf\""))
                .andExpect(result -> {
                    byte[] bytes = result.getResponse().getContentAsByteArray();
                    if (bytes.length <= 100) {
                        throw new AssertionError("PDF gerado com tamanho inesperado: " + bytes.length);
                    }
                });
    }
}
