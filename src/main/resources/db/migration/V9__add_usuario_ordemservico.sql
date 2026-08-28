-- Usuario vinculado a ordem de servico (quem registrou/e responsavel).
-- Coluna escalar, sem chave estrangeira, seguindo o padrao dos demais
-- vinculos legados do schema. Opcional (NULL): as OS ja existentes
-- permanecem validas.

ALTER TABLE ordemservico
    ADD COLUMN IF NOT EXISTS usuarioidusuario INT NULL;
