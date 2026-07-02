package br.com.deskinstaller.integration;

import br.com.deskinstaller.dto.OrdemServicoDTO;
import br.com.deskinstaller.model.Cliente;
import br.com.deskinstaller.model.Endereco;
import br.com.deskinstaller.model.Ordemservico;
import br.com.deskinstaller.repository.ClienteRepository;
import br.com.deskinstaller.repository.EnderecoRepository;
import br.com.deskinstaller.repository.OrdemServicoRepository;
import br.com.deskinstaller.service.OrdemservicoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class OrdemServicoServiceIntegrationTest {

    @Autowired
    private OrdemservicoService ordemservicoService;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Test
    void listarAtivasOuAtualizadasNosUltimos7Dias_usaDatasituacaoCorretamente() {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente OS");
        cliente.setTipoPessoa("F");
        cliente.setDataNascimento(new Date());
        cliente = clienteRepository.save(cliente);

        Endereco endereco = new Endereco();
        endereco.setLogradouro("Rua A");
        endereco.setNumero("123");
        endereco.setCidade("Sao Paulo");
        endereco.setCliente(cliente.getIdcliente());
        endereco = enderecoRepository.save(endereco);

        ordemServicoRepository.save(criarOrdem(cliente, endereco, "Aberta", diasAtras(30), diasAtras(30)));
        ordemServicoRepository.save(criarOrdem(cliente, endereco, "Finalizada", diasAtras(2), diasAtras(2)));
        ordemServicoRepository.save(criarOrdem(cliente, endereco, "Finalizada", new Date(), diasAtras(10)));

        List<OrdemServicoDTO> resultado = ordemservicoService.listarSituacaoNaoCanceladaOuFinalizadaUltimos7Dias();

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(os -> "Aberta".equals(os.getSituacao())));
        assertTrue(resultado.stream().anyMatch(os -> "Finalizada".equals(os.getSituacao()) && os.getDatasituacao().after(diasAtras(7))));
    }

    private Ordemservico criarOrdem(Cliente cliente, Endereco endereco, String situacao, Date dataServico, Date dataSituacao) {
        Ordemservico ordem = new Ordemservico();
        ordem.setHoraServico("08:00");
        ordem.setDataServico(dataServico);
        ordem.setValor(150.0);
        ordem.setSituacao(situacao);
        ordem.setDatasituacao(dataSituacao);
        ordem.setCliente(cliente);
        ordem.setEndereco(endereco);
        ordem.setRecebida(false);
        return ordem;
    }

    private Date diasAtras(int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -dias);
        return calendar.getTime();
    }
}
