package br.com.deskinstaller.service;

import br.com.deskinstaller.model.Usuario;
import br.com.deskinstaller.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Resolve o usuario logado a partir do SecurityContext (token JWT).
 *
 * O cliente nao decide quem e o usuario: quem grava um registro e sempre
 * quem esta autenticado. Quando a seguranca esta desligada
 * (app.security.enabled=false) nao ha autenticacao e os metodos devolvem
 * Optional.empty(), cabendo ao chamador definir o fallback.
 */
@Service
@RequiredArgsConstructor
public class UsuarioAutenticadoService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Optional<Usuario> usuarioAtual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return usuarioRepository.findByUsername(auth.getName());
    }

    @Transactional(readOnly = true)
    public Optional<Integer> idUsuarioAtual() {
        return usuarioAtual().map(Usuario::getId);
    }
}
