package br.com.deskinstaller.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
@RequiredArgsConstructor
@Slf4j
public class LegacySchemaCompatibilityConfig {

    private final DataSource dataSource;

    @jakarta.annotation.PostConstruct
    public void alignLegacySchema() {
        ensureLegacyOrdemServicoFuncionarioIsNullable();
    }

    private void ensureLegacyOrdemServicoFuncionarioIsNullable() {
        final String metadataSql = """
                SELECT is_nullable
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = 'ordemservico'
                  AND column_name = 'funcionario_idfuncionario'
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(metadataSql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            if (!resultSet.next()) {
                return;
            }

            if ("YES".equalsIgnoreCase(resultSet.getString("is_nullable"))) {
                return;
            }

            log.warn("Compatibilizando schema legado: tornando ordemservico.funcionario_idfuncionario anulavel.");
            try (Statement alterStatement = connection.createStatement()) {
                alterStatement.execute("ALTER TABLE ordemservico MODIFY COLUMN funcionario_idfuncionario INT NULL");
            }
        } catch (Exception ex) {
            log.error("Falha ao compatibilizar schema legado da tabela ordemservico.", ex);
        }
    }
}
