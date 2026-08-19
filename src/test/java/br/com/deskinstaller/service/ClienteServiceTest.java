package br.com.deskinstaller.service;

import br.com.deskinstaller.dto.ClienteDTO;
import br.com.deskinstaller.model.Cliente;
import br.com.deskinstaller.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

public class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarTodos_retornaLista() {
        Cliente c1 = new Cliente(); c1.setIdcliente(1); c1.setNome("A");
        Cliente c2 = new Cliente(); c2.setIdcliente(2); c2.setNome("B");
        when(repository.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<ClienteDTO> result = service.listarTodos();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void salvar_deveSalvar() {
        Cliente in = new Cliente(); in.setNome("C");
        Cliente saved = new Cliente(); saved.setIdcliente(10); saved.setNome(in.getNome());
        when(repository.save(any(Cliente.class))).thenReturn(saved);

        ClienteDTO dto = ClienteDTO.builder().nome(in.getNome()).build();
        ClienteDTO out = service.salvar(dto);

        assertNotNull(out);
        assertEquals(10, out.getIdcliente());
    }
}

