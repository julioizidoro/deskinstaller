package br.com.deskinstaller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Classe principal da aplicação DeskInstaller
 *
 * Atualizada para suportar empacotamento como WAR e deploy em Tomcat externo.
 *
 * @author Julio Izidoro
 * @since 2025-11-13
 */
@SpringBootApplication
public class DeskInstallerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(DeskInstallerApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(DeskInstallerApplication.class);
    }
}
