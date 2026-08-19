package br.com.deskinstaller.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Configuração de Conexão com Banco de Dados MySQL
 *
 * Esta classe configura:
 * - HikariCP (pool de conexões de alta performance)
 * - EntityManagerFactory (JPA)
 * - TransactionManager (gerenciamento de transações)
 *
 * @author Julio Izidoro
 * @since 2025-11-13
 */
@Configuration
@EnableTransactionManagement
@Slf4j
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.jpa.hibernate.ddl-auto:update}")
    private String ddlAuto;

    @Value("${spring.jpa.show-sql:false}")
    private boolean showSql;

    @Value("${spring.jpa.properties.hibernate.format_sql:true}")
    private boolean formatSql;

    @Value("${spring.jpa.database-platform:}")
    private String databasePlatform;

    @Value("${app.database.log-details-enabled:false}")
    private boolean logDetailsEnabled;

    private final HibernateExtraProperties hibernateExtraProperties;

    public DatabaseConfig(HibernateExtraProperties hibernateExtraProperties) {
        this.hibernateExtraProperties = hibernateExtraProperties;
    }

    /**
     * Configura o DataSource com HikariCP (pool de conexões)
     * HikariCP é conhecido por ser o pool mais rápido e confiável
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        log.info("Configurando DataSource com HikariCP");
        if (logDetailsEnabled && log.isDebugEnabled()) {
            log.debug("Database URL: {}", maskPassword(dbUrl));
            log.debug("Database User: {}", maskUsername(dbUsername));
        }

        HikariConfig hikariConfig = new HikariConfig();

        // Configurações básicas de conexão
        hikariConfig.setJdbcUrl(dbUrl);
        hikariConfig.setUsername(dbUsername);
        hikariConfig.setPassword(dbPassword);
        hikariConfig.setDriverClassName(driverClassName);

        // Pool de conexões - configurações otimizadas
        hikariConfig.setMaximumPoolSize(10); // Máximo de 10 conexões
        hikariConfig.setMinimumIdle(1);      // Mínimo de 1 conexão idle
        hikariConfig.setConnectionTimeout(30000); // 30 segundos timeout
        hikariConfig.setIdleTimeout(600000);      // 10 minutos idle
        hikariConfig.setMaxLifetime(1800000);     // 30 minutos max lifetime

        // Não falhar a inicialização do pool caso o banco esteja temporariamente indisponível
        // isso permite que o Tomcat e a aplicação subam; já o pool tentará conectar ao DB quando necessário
        hikariConfig.setInitializationFailTimeout(0);

        // Pool name para identificação nos logs
        hikariConfig.setPoolName("DeskInstaller-HikariCP");

        // Validations
        // Hikari usa isValid() por padrão; mantemos uma query simples para drivers que necessitem
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setValidationTimeout(5000); // 5 segundos

        // Auto commit (definir conforme necessidade da aplicação)
        hikariConfig.setAutoCommit(true);

        // Propriedades adicionais do MySQL
        if (driverClassName != null && driverClassName.toLowerCase().contains("mysql")) {
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
            hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
            hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
            hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
            hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
            hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
            hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
        }

        // opcional: tempo para detectar vazamentos
        hikariConfig.setLeakDetectionThreshold(15000);

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        log.info("DataSource configurado com sucesso.");
        log.debug("Pool Size: {} conexões", hikariConfig.getMaximumPoolSize());
        log.debug("Minimum Idle: {} conexões", hikariConfig.getMinimumIdle());

        return dataSource;
    }

    /**
     * Configura o EntityManagerFactory para JPA/Hibernate
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        log.info("Configurando EntityManagerFactory para JPA/Hibernate");

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);

        // Pacotes onde estão as entidades JPA (ajustado para o pacote atual do projeto)
        em.setPackagesToScan(
            "br.com.deskinstaller.model"
        );

        // Adapter do Hibernate
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(showSql);
        em.setJpaVendorAdapter(vendorAdapter);

        // Propriedades do Hibernate
        Properties jpaProperties = new Properties();
        if (databasePlatform != null && !databasePlatform.isBlank()) {
            jpaProperties.put("hibernate.dialect", databasePlatform);
        }
        jpaProperties.put("hibernate.hbm2ddl.auto", ddlAuto);
        jpaProperties.put("hibernate.show_sql", showSql);
        jpaProperties.put("hibernate.format_sql", formatSql);
        jpaProperties.put("hibernate.use_sql_comments", true);
        jpaProperties.put("hibernate.jdbc.batch_size", 20);
        jpaProperties.put("hibernate.order_inserts", true);
        jpaProperties.put("hibernate.order_updates", true);
        jpaProperties.put("hibernate.jdbc.time_zone", "America/Sao_Paulo");

        // Configurações de performance
        jpaProperties.put("hibernate.generate_statistics", false);
        jpaProperties.put("hibernate.cache.use_second_level_cache", false);
        jpaProperties.put("hibernate.cache.use_query_cache", false);

        // Tudo definido em spring.jpa.properties.* no application.properties vence
        // os defaults acima. E o que permite ajustar o Hibernate por configuracao,
        // sem precisar editar esta classe.
        jpaProperties.putAll(hibernateExtraProperties.getProperties());

        em.setJpaProperties(jpaProperties);

        log.info("EntityManagerFactory configurado.");
        if (logDetailsEnabled && log.isDebugEnabled()) {
            log.debug("DDL Auto: {}", ddlAuto);
            log.debug("Show SQL: {}", showSql);
            log.debug("Pacotes escaneados: br.com.deskinstaller.model");
        }

        return em;
    }

    /**
     * Configura o gerenciador de transações JPA
     */
    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        log.info("Configurando Transaction Manager");

        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());

        log.info("Transaction Manager configurado.");

        return transactionManager;
    }

    /**
     * Mascara a senha na URL do banco para logs
     */
    private String maskPassword(String url) {
        if (url == null) return null;
        return url.replaceAll("password=[^&;]*", "password=****");
    }

    private String maskUsername(String username) {
        if (username == null || username.isBlank()) {
            return "<vazio>";
        }
        if (username.length() <= 2) {
            return "**";
        }
        return username.charAt(0) + "***" + username.charAt(username.length() - 1);
    }
}
