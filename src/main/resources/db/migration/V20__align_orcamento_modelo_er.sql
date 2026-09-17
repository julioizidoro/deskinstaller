-- Alinha a tabela legada `orcamento` ao modelo ER / entidade Orcamento.
-- Mudancas: dataservico vira dataemissao, entra datavalidade e entra a FK de endereco.

-- Preserva os dados ja gravados em dataservico.
ALTER TABLE orcamento
    CHANGE COLUMN dataservico dataemissao DATE NULL DEFAULT NULL;

ALTER TABLE orcamento
    ADD COLUMN datavalidade DATE NULL DEFAULT NULL AFTER dataemissao;

-- Nullable porque as linhas existentes nao tem endereco definido.
ALTER TABLE orcamento
    ADD COLUMN endereco_idendereco INT NULL DEFAULT NULL AFTER cliente_idcliente;

ALTER TABLE orcamento
    ADD CONSTRAINT fk_orcamento_endereco1
        FOREIGN KEY (endereco_idendereco) REFERENCES endereco (idendereco);

-- As colunas horaServico, indicacao, status, funcionario_idfuncionario e
-- Funcionario_idFuncionario deixaram de ser mapeadas pela entidade. O Hibernate
-- ignora colunas extras na validacao, entao elas ficam no banco para nao perder
-- historico. Para remove-las de vez, rode manualmente:
-- ALTER TABLE orcamento
--     DROP COLUMN horaServico,
--     DROP COLUMN indicacao,
--     DROP COLUMN status,
--     DROP COLUMN funcionario_idfuncionario;
