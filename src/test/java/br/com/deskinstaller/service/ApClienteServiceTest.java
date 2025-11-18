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

    @InjectMocks
    private ApClienteService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarPorCliente_retornaLista() {
        Apcliente a1 = new Apcliente(1, new Date(), "NF1", "L1", new Date(), new Date(), "Local1", 2, "M1", "F1", "ME1", "NE1", "MC1", "NC1", "Cap1");
        Apcliente a2 = new Apcliente(2, new Date(), "NF2", "L2", new Date(), new Date(), "Local2", 2, "M2", "F2", "ME2", "NE2", "MC2", "NC2", "Cap2");
        when(repository.findByCliente(2)).thenReturn(Arrays.asList(a1, a2));

        List<ApclienteDTO> result = service.listarPorCliente(2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getCliente());
    }

    @Test
    void salvar_deveRetornarDtoSalvo() {
        Apcliente input = new Apcliente(null, new Date(), "NF1", "L1", new Date(), new Date(), "Local1", 3, "M1", "F1", "ME1", "NE1", "MC1", "NC1", "Cap1");
        Apcliente saved = new Apcliente(10, input.getDataCompra(), input.getNotaFiscal(), input.getLoja(), input.getDataInstalacao(), input.getDataManutencao(), input.getLocal(), input.getCliente(), input.getModelo(), input.getFabricante(), input.getModeloEvaporadora(), input.getNsEvaporadora(), input.getModeloCodensadora(), input.getNsCodensadora(), input.getCapacidade());
        when(repository.save(any(Apcliente.class))).thenReturn(saved);

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
        Apcliente a = new Apcliente(5, new Date(), "NF", "L", new Date(), new Date(), "Local", 4, "M", "F", "ME", "NE", "MC", "NC", "Cap");
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
