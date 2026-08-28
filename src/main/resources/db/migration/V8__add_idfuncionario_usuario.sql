-- Liga o usuario de acesso ao funcionario correspondente.
-- Coluna opcional (NULL): usuarios administrativos podem nao ter
-- funcionario vinculado, e as linhas ja existentes seguem validas.
-- Sem chave estrangeira, seguindo o padrao dos demais vinculos legados.

ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS idfuncionario INT NULL;
