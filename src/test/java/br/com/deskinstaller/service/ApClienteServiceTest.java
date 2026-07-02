package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.ApclienteDTO;
import br.com.deskinstaller.model.Apcliente;
import br.com.deskinstaller.repository.ApClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class ApClienteServiceTest {

    @Mock
    private ApClienteRepository repository;

    @Mock
    private DomainValidationService domainValidationService;

    @InjectMocks
    private ApClienteService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarPorCliente_retornaLista() {
        Apcliente a1 = Apcliente.builder()
            .idapCliente(1)
            .dataCompra(new Date())
            .notaFiscal("NF1")
            .loja("L1")
            .dataInstalacao(new Date())
            .dataManutencao(new Date())
            .local("Local1")
            .cliente(2)
            .modelo("M1")
            .fabricante("F1")
            .modeloEvaporadora("ME1")
            .nsEvaporadora("NE1")
            .modeloCodensadora("MC1")
            .nsCodensadora("NC1")
            .capacidade("Cap1")
            .build();
        Apcliente a2 = Apcliente.builder()
            .idapCliente(2)
            .dataCompra(new Date())
            .notaFiscal("NF2")
            .loja("L2")
            .dataInstalacao(new Date())
            .dataManutencao(new Date())
            .local("Local2")
            .cliente(2)
            .modelo("M2")
            .fabricante("F2")
            .modeloEvaporadora("ME2")
            .nsEvaporadora("NE2")
            .modeloCodensadora("MC2")
            .nsCodensadora("NC2")
            .capacidade("Cap2")
            .build();
        when(repository.findByCliente(2)).thenReturn(Arrays.asList(a1, a2));

        List<ApclienteDTO> result = service.listarPorCliente(2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getCliente());
    }

    @Test
    void salvar_deveRetornarDtoSalvo() {
        Apcliente input = Apcliente.builder()
            .dataCompra(new Date())
            .notaFiscal("NF1")
            .loja("L1")
            .dataInstalacao(new Date())
            .dataManutencao(new Date())
            .local("Local1")
            .cliente(3)
            .modelo("M1")
            .fabricante("F1")
            .modeloEvaporadora("ME1")
            .nsEvaporadora("NE1")
            .modeloCodensadora("MC1")
            .nsCodensadora("NC1")
            .capacidade("Cap1")
            .build();
        Apcliente saved = Apcliente.builder()
            .idapCliente(10)
            .dataCompra(input.getDataCompra())
            .notaFiscal(input.getNotaFiscal())
            .loja(input.getLoja())
            .dataInstalacao(input.getDataInstalacao())
            .dataManutencao(input.getDataManutencao())
            .local(input.getLocal())
            .cliente(input.getCliente())
            .modelo(input.getModelo())
            .fabricante(input.getFabricante())
            .modeloEvaporadora(input.getModeloEvaporadora())
            .nsEvaporadora(input.getNsEvaporadora())
            .modeloCodensadora(input.getModeloCodensadora())
            .nsCodensadora(input.getNsCodensadora())
            .capacidade(input.getCapacidade())
            .build();
        when(repository.save(any(Apcliente.class))).thenReturn(saved);
        when(domainValidationService.requireCliente(3)).thenReturn(new br.com.deskinstaller.model.Cliente());

        ApclienteDTO dto = ApclienteDTO.builder()
                .dataCompra(input.getDataCompra())
                .notaFiscal(input.getNotaFiscal())
                .loja(input.getLoja())
                .cliente(input.getCliente())
                .modelo(input.getModelo())
                .build();

        ApclienteDTO result = service.salvar(dto);

        assertNotNull(result);
        assertEquals(10, result.getIdapCliente());
        assertEquals(input.getCliente(), result.getCliente());
    }

    @Test
    void buscarPorId_sucesso() {
        Apcliente a = Apcliente.builder()
            .idapCliente(5)
            .dataCompra(new Date())
            .notaFiscal("NF")
            .loja("L")
            .dataInstalacao(new Date())
            .dataManutencao(new Date())
            .local("Local")
            .cliente(4)
            .modelo("M")
            .fabricante("F")
            .modeloEvaporadora("ME")
            .nsEvaporadora("NE")
            .modeloCodensadora("MC")
            .nsCodensadora("NC")
            .capacidade("Cap")
            .build();
        when(repository.findById(5)).thenReturn(Optional.of(a));

        Optional<ApclienteDTO> dto = service.buscarPorId(5);
        assertTrue(dto.isPresent());
        assertEquals(5, dto.get().getIdapCliente());
    }

    @Test
    void buscarPorId_naoEncontrado() {
        when(repository.findById(999)).thenReturn(Optional.empty());
        Optional<ApclienteDTO> dto = service.buscarPorId(999);
        assertFalse(dto.isPresent());
    }

    @Test
    void deletar_sucesso() {
        when(repository.existsById(3)).thenReturn(true);
        service.deletar(3);
        verify(repository, times(1)).deleteById(3);
    }

    @Test
    void deletar_naoEncontrado_deveLancar() {
        when(repository.existsById(777)).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deletar(777));
        assertTrue(ex.getMessage().contains("Apcliente não encontrado"));
    }

}
