-- O sistema deixou de usar papeis: todo usuario autenticado tem o mesmo
-- nivel de acesso. As tabelas de papel criadas na V2 sao descartadas.
-- A ordem importa: a tabela de vinculo sai antes da tabela referenciada.

DROP TABLE IF EXISTS usuario_role;
DROP TABLE IF EXISTS role;
