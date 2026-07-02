package br.com.deskinstaller.integration;

import br.com.deskinstaller.model.Role;
import br.com.deskinstaller.model.Usuario;
import br.com.deskinstaller.repository.RoleRepository;
import br.com.deskinstaller.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.security.enabled=true",
        "app.security.jwt.secret=ZGVza2luc3RhbGxlci10ZXN0LWp3dC1zZWNyZXQtdmVyeS1sb25nLWFhYWFhYWFhYWE=",
        "spring.autoconfigure.exclude="
})
@AutoConfigureMockMvc
@Transactional
class SecurityAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupUser() {
        Role financeiro = roleRepository.findByName("FINANCEIRO")
                .orElseGet(() -> roleRepository.save(new Role(null, "FINANCEIRO", "Financeiro")));

        Usuario usuario = usuarioRepository.findByUsername("financeiro")
                .orElseGet(Usuario::new);
        usuario.setUsername("financeiro");
        usuario.setPassword(passwordEncoder.encode("fin123"));
        usuario.setAtivo(true);
        usuario.setRoles(Set.of(financeiro));
        usuarioRepository.save(usuario);
    }

    @Test
    void usuarioFinanceiroNaoDeveAcessarFuncionarios() throws Exception {
        String token = obterToken("financeiro", "fin123");

        mockMvc.perform(get("/api/funcionarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void usuarioFinanceiroDeveAcessarClientesParaLeitura() throws Exception {
        String token = obterToken("financeiro", "fin123");

        mockMvc.perform(get("/api/clientes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private String obterToken(String username, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return response.replaceAll("(?s).*\"token\"\\s*:\\s*\"([^\"]+)\".*", "$1");
    }
}
