-- Cliente devedor do titulo a receber.
-- Obrigatorio: todo titulo pertence a um cliente.

ALTER TABLE contasreceber
    ADD COLUMN IF NOT EXISTS clienteidcliente INT NOT NULL;

ALTER TABLE contasreceber
    ADD CONSTRAINT fk_contasreceber_cliente1
        FOREIGN KEY (clienteidcliente) REFERENCES cliente (idcliente);
