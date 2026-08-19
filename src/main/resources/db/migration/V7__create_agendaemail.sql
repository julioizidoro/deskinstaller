-- Lista de enderecos de e-mail que recebem as agendas enviadas pelo sistema.
-- Registros nao sao removidos: a saida de circulacao e feita via coluna ativo.

CREATE TABLE IF NOT EXISTS agendaemail (
    idagendaemail INT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255) NULL,
    ativo         TINYINT NULL DEFAULT 0
);
