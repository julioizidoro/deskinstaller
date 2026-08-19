package br.com.deskinstaller;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Ponto de entrada quando a aplicacao e empacotada como WAR e implantada em um
 * Tomcat externo. O metodo main da {@link DeskInstallerApplication} continua
 * valendo para execucao local (mvn spring-boot:run).
 *
 * <p>Atencao: no deploy como WAR o Tomcat define o context-path a partir do nome
 * do arquivo (deskinstaller-api.war -> /deskinstaller-api). A propriedade
 * server.servlet.context-path do application.properties e ignorada nesse modo.
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DeskInstallerApplication.class);
    }
}
