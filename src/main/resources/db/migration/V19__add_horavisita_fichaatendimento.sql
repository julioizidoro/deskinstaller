-- Hora da visita (HH:mm) na ficha de atendimento.
ALTER TABLE fichaatendimento
    ADD COLUMN horavisita VARCHAR(5) NULL DEFAULT NULL AFTER datavisita;

-- Situacao da ficha (ex.: ABERTA, CONCLUIDA, CANCELADA).
ALTER TABLE fichaatendimento
    ADD COLUMN situacao VARCHAR(20) NULL DEFAULT NULL AFTER horavisita;
