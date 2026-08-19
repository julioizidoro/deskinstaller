package br.com.deskinstaller.controller;

import br.com.deskinstaller.config.DatabaseConnectionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para monitoramento da conexão com o banco de dados
 *
 * @author Julio Izidoro
 * @since 2025-11-13
 */
@RestController
@RequestMapping("/api/database")
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.database.monitoring.enabled", havingValue = "true")
public class DatabaseMonitorController {

    private final DataSource dataSource;
    private final DatabaseConnectionValidator validator;

    /**
     * Endpoint para verificar o status da conexão com o banco
     * GET /api/database/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getDatabaseStatus() {
        Map<String, Object> status = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            status.put("connected", true);
            status.put("database", metaData.getDatabaseProductName());
            status.put("version", metaData.getDatabaseProductVersion());
            status.put("driver", metaData.getDriverName());
            status.put("driverVersion", metaData.getDriverVersion());
            status.put("url", maskUrl(metaData.getURL()));
            status.put("username", metaData.getUserName());
            status.put("catalog", connection.getCatalog());
            status.put("schema", connection.getSchema());
            status.put("autoCommit", connection.getAutoCommit());
            status.put("readOnly", connection.isReadOnly());
            status.put("valid", connection.isValid(5));

            log.info("Database status checked: Connected to {}", metaData.getDatabaseProductName());

            return ResponseEntity.ok(status);

        } catch (Exception e) {
            log.error("Error checking database status", e);
            status.put("connected", false);
            status.put("error", e.getMessage());
            return ResponseEntity.status(503).body(status);
        }
    }

    /**
     * Endpoint para verificar informações do pool de conexões
     * GET /api/database/pool
     */
    @GetMapping("/pool")
    public ResponseEntity<Map<String, Object>> getPoolInfo() {
        Map<String, Object> poolInfo = new HashMap<>();

        try {
            String poolStatus = validator.getPoolStatus();
            poolInfo.put("status", poolStatus);
            poolInfo.put("healthy", validator.testConnection());

            return ResponseEntity.ok(poolInfo);

        } catch (Exception e) {
            log.error("Error getting pool info", e);
            poolInfo.put("error", e.getMessage());
            return ResponseEntity.status(503).body(poolInfo);
        }
    }

    /**
     * Endpoint para testar a conexão
     * GET /api/database/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> result = new HashMap<>();

        boolean isValid = validator.testConnection();
        result.put("connectionValid", isValid);
        result.put("timestamp", java.time.OffsetDateTime.now().toString());

        if (isValid) {
            log.info("Database connection test: SUCCESS");
            return ResponseEntity.ok(result);
        } else {
            log.warn("Database connection test: FAILED");
            return ResponseEntity.status(503).body(result);
        }
    }

    /**
     * Endpoint para obter informações sobre as tabelas
     * GET /api/database/tables
     */
    @GetMapping("/tables")
    public ResponseEntity<Map<String, Object>> getTables() {
        Map<String, Object> response = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();

            java.util.List<String> tables = new java.util.ArrayList<>();
            try (java.sql.ResultSet rs = metaData.getTables(catalog, null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }
            }

            response.put("catalog", catalog);
            response.put("tables", tables);
            response.put("count", tables.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error listing tables", e);
            response.put("error", e.getMessage());
            return ResponseEntity.status(503).body(response);
        }
    }

    private String maskUrl(String url) {
        if (url == null) return null;
        return url.replaceAll("password=[^&;]*", "password=****");
    }
}
