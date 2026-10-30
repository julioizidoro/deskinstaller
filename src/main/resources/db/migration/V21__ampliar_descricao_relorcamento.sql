-- A descricao dos itens do orcamento passa a aceitar textos mais longos (ate 255 caracteres).
ALTER TABLE relorcamento
    MODIFY COLUMN descricao VARCHAR(255) NULL DEFAULT NULL;
