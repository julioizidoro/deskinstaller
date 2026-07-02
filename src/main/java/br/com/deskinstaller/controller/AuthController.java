package br.com.deskinstaller.controller;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
public class AuthController {

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
            throw new ResponseStatusException(UNAUTHORIZED, "Credenciais inválidas");
        }

        if (!passwordEncoder.matches(request.password(), userDetails.getPassword())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Credenciais inválidas");
        }

        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Credenciais inválidas"));

        String token = jwtTokenService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.issueToken(usuario);
        return ResponseEntity.ok(buildLoginResponse(userDetails.getUsername(), token, refreshToken.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO request) {
        RefreshToken refreshToken = refreshTokenService.validate(request.refreshToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(refreshToken.getUsuario().getUsername());

        String accessToken = jwtTokenService.generateToken(userDetails);
        RefreshToken rotatedToken = refreshTokenService.rotate(refreshToken);

        return ResponseEntity.ok(buildLoginResponse(userDetails.getUsername(), accessToken, rotatedToken.getToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDTO request) {
        RefreshToken refreshToken = refreshTokenService.validate(request.refreshToken());
        refreshTokenService.revoke(refreshToken);
        return ResponseEntity.noContent().build();
    }

    private LoginResponseDTO buildLoginResponse(String username, String accessToken, String refreshToken) {
        return new LoginResponseDTO(
                accessToken,
                refreshToken,
                "Bearer",
                jwtTokenService.getExpirationSeconds(),
                refreshTokenService.getRefreshTokenExpirationSeconds(),
                username
        );
    }
}
