package br.com.deskinstaller.repository;

import br.com.deskinstaller.model.RefreshToken;
import br.com.deskinstaller.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUsuarioAndRevokedFalse(Usuario usuario);
}
