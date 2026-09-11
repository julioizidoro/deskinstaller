-- Forma de cobranca da ordem de servico (ex.: AVISTA, PARCELADO).
ALTER TABLE ordemservico ADD COLUMN tipocobranca VARCHAR(10) NULL;
