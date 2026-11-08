-- Data/hora do aviso de agendamento enviado por WhatsApp (GET /api/ordens-servico/comunicar).
-- Evita reenviar o aviso da mesma OS no mesmo dia.
ALTER TABLE ordemservico
    ADD COLUMN IF NOT EXISTS dataAvisoWhatsapp DATETIME NULL;
