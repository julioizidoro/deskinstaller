-- Status informado pelo cliente para o agendamento da OS
-- (ex.: Confirmado, Cancelamento solicitado). Opcional (NULL):
-- as OS ja existentes permanecem validas.

ALTER TABLE ordemservico
    ADD COLUMN IF NOT EXISTS statuscliente VARCHAR(15) NULL;
