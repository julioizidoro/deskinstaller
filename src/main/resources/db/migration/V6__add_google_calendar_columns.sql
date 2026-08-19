-- Colunas de apoio a integracao com o Google Calendar.
-- ordemservico.email: endereco convidado para o evento da agenda.
-- ordemservico.googleEventId: liga a OS ao evento criado, permitindo
-- atualizar e remover o evento quando a ordem muda.

ALTER TABLE ordemservico
    ADD COLUMN IF NOT EXISTS email VARCHAR(255) NULL;

ALTER TABLE ordemservico
    ADD COLUMN IF NOT EXISTS googleEventId VARCHAR(255) NULL;
