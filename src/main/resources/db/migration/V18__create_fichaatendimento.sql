-- Ficha de atendimento (visita tecnica) e seus itens de servico.
CREATE TABLE IF NOT EXISTS fichaatendimento (
    idfichaatendimento       INT          NOT NULL AUTO_INCREMENT,
    datavisita               DATE         NULL DEFAULT NULL,
    observacao               MEDIUMTEXT   NULL DEFAULT NULL,
    funcionarioidfuncionario INT          NOT NULL,
    clienteidcliente         INT          NOT NULL,
    enderecoidendereco       INT          NOT NULL,
    PRIMARY KEY (idfichaatendimento),
    KEY fk_fichaatendimento_funcionario1_idx (funcionarioidfuncionario),
    KEY fk_fichaatendimento_cliente1_idx (clienteidcliente),
    KEY fk_fichaatendimento_endereco1_idx (enderecoidendereco),
    CONSTRAINT fk_fichaatendimento_funcionario1
        FOREIGN KEY (funcionarioidfuncionario) REFERENCES funcionario (idfuncionario)
        ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_fichaatendimento_cliente1
        FOREIGN KEY (clienteidcliente) REFERENCES cliente (idcliente)
        ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_fichaatendimento_endereco1
        FOREIGN KEY (enderecoidendereco) REFERENCES endereco (idendereco)
        ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Nome da tabela mantido como veio do modelo original (typo "sdrvico" preservado).
CREATE TABLE IF NOT EXISTS fichaatendimentosdrvico (
    idfichaatendimentosdrvico          INT          NOT NULL AUTO_INCREMENT,
    quantidade                         DOUBLE       NULL DEFAULT NULL,
    descricao                          VARCHAR(255) NULL DEFAULT NULL,
    fichaatendimentoidfichaatendimento INT          NOT NULL,
    PRIMARY KEY (idfichaatendimentosdrvico),
    KEY fk_fichaatendimentosdrvico_fichaatendimento1_idx (fichaatendimentoidfichaatendimento),
    CONSTRAINT fk_fichaatendimentosdrvico_fichaatendimento1
        FOREIGN KEY (fichaatendimentoidfichaatendimento) REFERENCES fichaatendimento (idfichaatendimento)
        ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
