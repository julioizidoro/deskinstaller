package br.com.deskinstaller.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/",
            "/index.html",
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/logout",
            // O navegador chega aqui redirecionado pelo Google, sem token da API.
            // A protecao e o parametro "state" validado em GoogleOAuthService.
            "/api/google/calendar/callback",
            // Links de agendamento abertos pelo cliente a partir do WhatsApp,
            // sem token da API. Atencao: sao publicos por id da OS.
            "/api/ordens-servico/*/confirmar",
            "/api/ordens-servico/*/cancelar",
            "/api/ordemservico/*/confirmar",
            "/api/ordemservico/*/cancelar",
            // Agenda do dia consultada pelo batch de aviso no WhatsApp, sem sessao.
            "/api/ordens-servico/data",
            "/api/ordemservico/data",
            // Paginas de agendamento do front consultam a OS sem token.
            "/api/confirmacao/*",
            "/api/cancelamento/*",
            // Integracao com o servidor de WhatsApp: chamada por processos e
            // paginas sem sessao. Atencao: publica, sem token da API — quem
            // alcancar a rede alcanca o envio.
            "/api/whatsapp/**"
    };

    private static final String[] DOCS_ENDPOINTS = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Value("${app.security.public-docs-enabled:true}")
    private boolean publicDocsEnabled;

    public SecurityConfig(RestAuthenticationEntryPoint authenticationEntryPoint,
                          RestAccessDeniedHandler accessDeniedHandler) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(publicDocsEnabled ? DOCS_ENDPOINTS : new String[]{}).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // O sistema nao usa papeis: qualquer usuario autenticado acessa a API.
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; frame-ancestors 'none'; form-action 'self'"))
                        .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                        .frameOptions(frame -> frame.deny())
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(Duration.ofDays(365).toSeconds()))
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
