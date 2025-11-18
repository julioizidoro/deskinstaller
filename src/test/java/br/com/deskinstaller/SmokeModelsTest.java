package br.com.deskinstaller;

import br.com.deskinstaller.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SmokeModelsTest {

    @Test
    void instantiateModels() {
        Cliente c = new Cliente();
        assertNotNull(c);
        Endereco e = new Endereco();
        assertNotNull(e);
        Funcionario f = new Funcionario();
        assertNotNull(f);
        Apcliente ap = new Apcliente();
        assertNotNull(ap);
        Servico s = new Servico();
        assertNotNull(s);
        // call some setters/getters
        c.setNome("X");
        e.setLogradouro("Y");
        f.setNome("Z");
        ap.setLocal("L");
        s.setDescricao("S");
    }
}

