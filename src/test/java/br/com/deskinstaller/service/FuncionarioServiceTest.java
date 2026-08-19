package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.FuncionarioDTO;
import br.com.deskinstaller.model.Funcionario;
import br.com.deskinstaller.repository.FuncionarioRepository;
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
import static org.mockito.ArgumentMatchers.any;

public class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository repository;

    @InjectMocks
    private FuncionarioService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarTodos_retornaLista() {
        Funcionario f1 = new Funcionario(1, "A", "123", 10f, null, false);
        Funcionario f2 = new Funcionario(2, "B", "456", 20f, null, false);
        when(repository.findAll()).thenReturn(Arrays.asList(f1, f2));

        List<FuncionarioDTO> result = service.listarTodos();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void salvar_deveSalvar() {
        Funcionario in = new Funcionario(null, "C", "789", 5f, null, true);
        Funcionario saved = new Funcionario(10, in.getNome(), in.getFoneCelular(), in.getValorComissao(), in.getFuncao(), in.isAtivo());
        when(repository.save(any(Funcionario.class))).thenReturn(saved);

        FuncionarioDTO dto = FuncionarioDTO.builder().nome(in.getNome()).build();
        FuncionarioDTO out = service.salvar(dto);

        assertNotNull(out);
        assertEquals(10, out.getIdfuncionario());
    }

    @Test
    void buscarPorId_sucesso() {
        Funcionario f = new Funcionario(5, "X", "000", 0f, null, true);
        when(repository.findById(5)).thenReturn(Optional.of(f));

        Optional<FuncionarioDTO> dto = service.buscarPorId(5);
        assertTrue(dto.isPresent());
        assertEquals(5, dto.get().getIdfuncionario());
    }

    @Test
    void buscarPorId_naoEncontrado() {
        when(repository.findById(999)).thenReturn(Optional.empty());
        Optional<FuncionarioDTO> dto = service.buscarPorId(999);
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
        assertTrue(ex.getMessage().contains("Funcionario não encontrado"));
    }

}
