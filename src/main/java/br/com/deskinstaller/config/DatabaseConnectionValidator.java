package br.com.deskinstaller.config;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe para validação e monitoramento da conexão com o banco de dados
 *
 * @author Julio Izidoro
 * @since 2025-11-13
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseConnectionValidator {

    private final DataSource dataSource;

    @Value("${app.database.validation.enabled:true}")
    private boolean validationEnabled;

    /**
     * Valida a conexão com o banco após a aplicação iniciar
     */
    @EventListener(ApplicationReadyEvent.class)
    public void validateDatabaseConnection() {
        if (!validationEnabled) {
            log.info("Validação de conexão com banco desabilitada por configuração.");
            return;
        }

        log.info("═══════════════════════════════════════════════════════");
        log.info("    VALIDANDO CONEXÃO COM BANCO DE DADOS");
        log.info("═══════════════════════════════════════════════════════");

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            log.info("✅ Conexão com banco de dados estabelecida com sucesso!");
            log.info("");
            log.info("📊 Informações do Banco de Dados:");
            log.info("   - Database Product: {}", metaData.getDatabaseProductName());
            log.info("   - Database Version: {}", metaData.getDatabaseProductVersion());
            log.info("   - Driver Name: {}", metaData.getDriverName());
            log.info("   - Driver Version: {}", metaData.getDriverVersion());
            log.info("   - URL: {}", maskUrl(metaData.getURL()));
            log.info("   - Username: {}", metaData.getUserName());
            log.info("   - Catalog (Database): {}", connection.getCatalog());
            log.info("   - Schema: {}", connection.getSchema());
            log.info("   - Auto Commit: {}", connection.getAutoCommit());
            log.info("   - Read Only: {}", connection.isReadOnly());
            log.info("   - Transaction Isolation: {}", getTransactionIsolationName(connection.getTransactionIsolation()));

            // Informações do HikariCP
            if (dataSource instanceof HikariDataSource) {
                logHikariStats((HikariDataSource) dataSource);
            }

            // Listar tabelas existentes
            logExistingTables(connection);

            log.info("═══════════════════════════════════════════════════════");

        } catch (SQLException e) {
            log.error("❌ ERRO ao conectar ao banco de dados!");
            log.error("   Mensagem: {}", e.getMessage());
            log.error("   SQL State: {}", e.getSQLState());
            log.error("   Error Code: {}", e.getErrorCode());
            log.error("");
            log.error("Verifique:");
            log.error("   1. Se o MySQL está rodando: brew services list (macOS)");
            log.error("   2. Se o banco 'dk_db' existe");
            log.error("   3. Se o usuário 'julioizidoro' tem permissões");
            log.error("   4. Se a senha está correta no application.properties");
            log.error("═══════════════════════════════════════════════════════");
            throw new RuntimeException("Falha na conexão com o banco de dados", e);
        }
    }

    /**
     * Loga estatísticas do pool de conexões HikariCP
     */
    private void logHikariStats(HikariDataSource hikariDataSource) {
        log.info("");
        log.info("🏊 Pool de Conexões HikariCP:");

        try {
            HikariPoolMXBean poolMXBean = hikariDataSource.getHikariPoolMXBean();

            log.info("   - Pool Name: {}", hikariDataSource.getPoolName());
            log.info("   - Conexões Ativas: {}", poolMXBean.getActiveConnections());
            log.info("   - Conexões Idle: {}", poolMXBean.getIdleConnections());
            log.info("   - Conexões Totais: {}", poolMXBean.getTotalConnections());
            log.info("   - Threads Aguardando: {}", poolMXBean.getThreadsAwaitingConnection());
            log.info("   - Máximo de Conexões: {}", hikariDataSource.getMaximumPoolSize());
            log.info("   - Mínimo Idle: {}", hikariDataSource.getMinimumIdle());
            log.info("   - Connection Timeout: {} ms", hikariDataSource.getConnectionTimeout());
            log.info("   - Idle Timeout: {} ms", hikariDataSource.getIdleTimeout());
            log.info("   - Max Lifetime: {} ms", hikariDataSource.getMaxLifetime());

        } catch (Exception e) {
            log.warn("   Não foi possível obter estatísticas do HikariCP: {}", e.getMessage());
        }
    }

    /**
     * Lista as tabelas existentes no banco de dados
     */
    private void logExistingTables(Connection connection) {
        log.info("");
        log.info("📋 Tabelas Existentes no Banco:");

        try {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();

            try (ResultSet tables = metaData.getTables(catalog, null, "%", new String[]{"TABLE"})) {
                int count = 0;
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    log.info("   - {}", tableName);
                    count++;
                }

                if (count == 0) {
                    log.info("   ⚠️  Nenhuma tabela encontrada.");
                    log.info("   As tabelas serão criadas automaticamente pelo Hibernate.");
                } else {
                    log.info("   Total: {} tabelas", count);
                }
            }

        } catch (SQLException e) {
            log.warn("   Não foi possível listar as tabelas: {}", e.getMessage());
        }
    }

    /**
     * Retorna o nome do nível de isolamento de transação
     */
    private String getTransactionIsolationName(int level) {
        switch (level) {
            case Connection.TRANSACTION_NONE:
                return "NONE";
            case Connection.TRANSACTION_READ_UNCOMMITTED:
                return "READ_UNCOMMITTED";
            case Connection.TRANSACTION_READ_COMMITTED:
                return "READ_COMMITTED";
            case Connection.TRANSACTION_REPEATABLE_READ:
                return "REPEATABLE_READ";
            case Connection.TRANSACTION_SERIALIZABLE:
                return "SERIALIZABLE";
            default:
                return "UNKNOWN (" + level + ")";
        }
    }

    /**
     * Mascara informações sensíveis da URL
     */
    private String maskUrl(String url) {
        if (url == null) return null;
        return url.replaceAll("password=[^&;]*", "password=****");
    }

    /**
     * Método público para testar conexão sob demanda
     */
    public boolean testConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(5); // timeout de 5 segundos
        } catch (SQLException e) {
            log.error("Erro ao testar conexão: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Retorna informações sobre o pool de conexões
     */
    public String getPoolStatus() {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDS = (HikariDataSource) dataSource;
            HikariPoolMXBean pool = hikariDS.getHikariPoolMXBean();

            return String.format(
                "Pool: %s | Ativas: %d | Idle: %d | Total: %d | Aguardando: %d | Max: %d",
                hikariDS.getPoolName(),
                pool.getActiveConnections(),
                pool.getIdleConnections(),
                pool.getTotalConnections(),
                pool.getThreadsAwaitingConnection(),
                hikariDS.getMaximumPoolSize()
            );
        }
        return "DataSource type: " + dataSource.getClass().getName();
    }
}
