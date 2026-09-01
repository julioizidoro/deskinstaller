package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.auth.AlterarSenhaRequestDTO;
import br.com.deskinstaller.dto.auth.LoginRequestDTO;
import br.com.deskinstaller.dto.auth.LoginResponseDTO;
import br.com.deskinstaller.dto.auth.RefreshTokenRequestDTO;
import br.com.deskinstaller.model.RefreshToken;
import br.com.deskinstaller.model.Usuario;
import br.com.deskinstaller.repository.UsuarioRepository;
import br.com.deskinstaller.service.JwtTokenService;
import br.com.deskinstaller.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
public class AuthController {

    /**
     * Hash descartavel usado quando o usuario nao existe, para que o tempo de
     * resposta do login nao revele se um username esta cadastrado.
     */
    private static final String HASH_DUMMY =
            "{bcrypt}$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(request.username());
        } catch (UsernameNotFoundException ex) {
            // Compara mesmo assim para manter o custo de tempo equivalente ao de um usuario existente.
            passwordEncoder.matches(request.password(), HASH_DUMMY);
            throw new ResponseStatusException(UNAUTHORIZED, "Credenciais inválidas");
        }

        if (!passwordEncoder.matches(request.password(), userDetails.getPassword())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Credenciais inválidas");
        }

        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Credenciais inválidas"));

        String token = jwtTokenService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.issueToken(usuario);
        return ResponseEntity.ok(buildLoginResponse(
                userDetails.getUsername(), token, refreshToken.getToken(),
                usuario.getId(), usuario.getIdfuncionario()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO request) {
        RefreshToken refreshToken = refreshTokenService.validate(request.refreshToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(refreshToken.getUsuario().getUsername());

        String accessToken = jwtTokenService.generateToken(userDetails);
        RefreshToken rotatedToken = refreshTokenService.rotate(refreshToken);

        return ResponseEntity.ok(buildLoginResponse(
                userDetails.getUsername(), accessToken, rotatedToken.getToken(),
                refreshToken.getUsuario().getId(),
                refreshToken.getUsuario().getIdfuncionario()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDTO request) {
        RefreshToken refreshToken = refreshTokenService.validate(request.refreshToken());
        refreshTokenService.revoke(refreshToken);
        return ResponseEntity.noContent().build();
    }

    /**
     * Troca a senha do usuario logado.
     * <p>
     * A senha atual e conferida aqui no backend; o username/idusuario do corpo
     * precisam bater com o usuario do token (o corpo nunca decide de quem e a senha).
     * Ao final, todos os refresh tokens do usuario sao revogados.
     */
    @PostMapping("/alterar-senha")
    public ResponseEntity<Void> alterarSenha(@Valid @RequestBody AlterarSenhaRequestDTO request,
                                             Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Autenticação necessária");
        }

        String usernameAutenticado = authentication.getName();
        if (!usernameAutenticado.equals(request.username())) {
            throw new ResponseStatusException(FORBIDDEN, "Não é permitido alterar a senha de outro usuário");
        }

        Usuario usuario = usuarioRepository.findByUsername(usernameAutenticado)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Usuário não encontrado"));

        if (request.idusuario() != null && !request.idusuario().equals(usuario.getId())) {
            throw new ResponseStatusException(FORBIDDEN, "Não é permitido alterar a senha de outro usuário");
        }

        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getPassword())) {
            throw new ResponseStatusException(BAD_REQUEST, "Senha atual incorreta");
        }

        if (passwordEncoder.matches(request.novaSenha(), usuario.getPassword())) {
            throw new ResponseStatusException(BAD_REQUEST, "A nova senha deve ser diferente da senha atual");
        }

        usuario.setPassword(passwordEncoder.encode(request.novaSenha()));
        usuarioRepository.save(usuario);

        // Invalida as sessoes existentes: nenhum refresh token antigo renova o acesso
        refreshTokenService.revokeAll(usuario);

        return ResponseEntity.noContent().build();
    }

    private LoginResponseDTO buildLoginResponse(String username, String accessToken, String refreshToken,
                                                Integer idusuario, Integer idfuncionario) {
        return new LoginResponseDTO(
                accessToken,
                refreshToken,
                "Bearer",
                jwtTokenService.getExpirationSeconds(),
                refreshTokenService.getRefreshTokenExpirationSeconds(),
                username,
                idusuario,
                idfuncionario
        );
    }
}
