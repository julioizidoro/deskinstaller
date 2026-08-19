package br.com.deskinstaller.config;

import br.com.deskinstaller.model.Role;
import br.com.deskinstaller.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class SecurityBootstrap {

    private final RoleRepository roleRepository;

    @PostConstruct
    @Transactional
    public void initializeSecurityData() {
        Map<String, String> roles = new LinkedHashMap<>();
        roles.put("ADMIN", "Administrador da plataforma");
        roles.put("ATENDENTE", "Atendimento e cadastro");
        roles.put("TECNICO", "Execução técnica");
        roles.put("FINANCEIRO", "Operações financeiras");

        roles.forEach(this::createRoleIfNeeded);
        log.info("Perfis de acesso básicos validados para autenticação JWT.");
    }

    private void createRoleIfNeeded(String name, String description) {
        roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            return roleRepository.save(role);
        });
    }
}
