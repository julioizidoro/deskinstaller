-- Data em que o titulo a receber foi efetivamente recebido.
-- Nulo enquanto o titulo estiver em aberto.

ALTER TABLE contasreceber
    ADD COLUMN IF NOT EXISTS datarecebimento DATE DEFAULT NULL;
