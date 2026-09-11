-- Rode este script no dk_db ANTES de subir a API.
-- O projeto usa JPA_DDL_AUTO=validate e FLYWAY_ENABLED=false, entao a migration
-- V15 nao e aplicada sozinha: sem esta tabela o Hibernate barra a subida com
-- "Schema-validation: missing table [arquivo]".
--   mysql -u julioizidoro -p dk_db < criar-tabela-arquivo.sql

-- Metadados dos arquivos enviados (binario vive no S3 ou no disco local).
CREATE TABLE IF NOT EXISTS arquivo (
    idarquivo         INT           NOT NULL AUTO_INCREMENT,
    provider          VARCHAR(10)   NOT NULL,
    bucket            VARCHAR(120)  NULL,
    chave             VARCHAR(500)  NOT NULL,
    pasta             VARCHAR(400)  NOT NULL,
    nome_original     VARCHAR(255)  NOT NULL,
    nome_armazenado   VARCHAR(255)  NOT NULL,
    content_type      VARCHAR(150)  NULL,
    tamanho           BIGINT        NULL,
    ref_tipo          VARCHAR(40)   NULL,
    ref_id            INT           NULL,
    descricao         VARCHAR(255)  NULL,
    data_upload       DATETIME      NOT NULL,
    usuario_idusuario INT           NULL,
    PRIMARY KEY (idarquivo),
    -- chave tem 500 chars; o indice unico usa um prefixo de 191 para caber no limite do utf8mb4
    UNIQUE KEY uk_arquivo_chave (chave(191)),
    KEY ix_arquivo_ref (ref_tipo, ref_id),
    KEY ix_arquivo_pasta (pasta(191)),
    KEY ix_arquivo_usuario (usuario_idusuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
