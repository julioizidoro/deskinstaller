package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.EnderecoDTO;
import br.com.deskinstaller.model.Endereco;
import br.com.deskinstaller.repository.EnderecoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.anyInt;

public class EnderecoServiceTest {

    @Mock
    private EnderecoRepository repository;

    @InjectMocks
    private EnderecoService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarPorCliente_retornaEnderecos() {
        Endereco e1 = new Endereco(1, "RUA", "A", "1", "", "Bairro", "00000-000", "Cidade", "UF", "", "", null, true, 2);
        Endereco e2 = new Endereco(2, "RUA", "B", "2", "", "Bairro", "00000-001", "Cidade", "UF", "", "", null, true, 2);
        when(repository.findByCliente(2)).thenReturn(Arrays.asList(e1, e2));

        List<EnderecoDTO> result = service.listarPorCliente(2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getCliente());
    }

    @Test
    void buscarPorId_sucesso() {
        Endereco e = new Endereco(5, "RUA", "X", "1", "", "Bairro", "00000-000", "Cidade", "UF", "", "", null, true, 3);
        when(repository.findById(5)).thenReturn(Optional.of(e));

        Optional<EnderecoDTO> dto = service.buscarPorId(5);
        assertTrue(dto.isPresent());
        assertEquals(5, dto.get().getIdendereco());
    }

    @Test
    void buscarPorId_naoEncontrado() {
        when(repository.findById(999)).thenReturn(Optional.empty());
        Optional<EnderecoDTO> dto = service.buscarPorId(999);
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
        assertTrue(ex.getMessage().contains("Endereco não encontrado"));
    }

}
