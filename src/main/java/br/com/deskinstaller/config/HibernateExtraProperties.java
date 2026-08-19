package br.com.deskinstaller.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Captura tudo que estiver sob {@code spring.jpa.properties.*}.
 *
 * <p>Existe porque {@link DatabaseConfig} constroi o EntityManagerFactory
 * manualmente: sem esta ponte, qualquer ajuste feito no application.properties
 * seria silenciosamente ignorado, o que ja custou horas de diagnostico.
 */
@Component
@ConfigurationProperties(prefix = "spring.jpa")
@Getter
@Setter
public class HibernateExtraProperties {

    /** Chaves relativas, por exemplo {@code hibernate.jdbc.time_zone}. */
    private Map<String, String> properties = new LinkedHashMap<>();
}
