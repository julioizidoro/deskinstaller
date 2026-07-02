SET @has_legacy_column := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'ordemservico'
      AND column_name = 'funcionario_idfuncionario'
);

SET @legacy_column_is_nullable := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'ordemservico'
      AND column_name = 'funcionario_idfuncionario'
      AND is_nullable = 'YES'
);

SET @sql := IF(
    @has_legacy_column = 1 AND @legacy_column_is_nullable = 0,
    'ALTER TABLE ordemservico MODIFY COLUMN funcionario_idfuncionario INT NULL',
    'SELECT 1'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
