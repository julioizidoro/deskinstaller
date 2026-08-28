-- Titulos a receber e o vinculo com as ordens de servico.

CREATE TABLE IF NOT EXISTS contasreceber (
    idcontasreceber INT AUTO_INCREMENT PRIMARY KEY,
    dataemissao DATE DEFAULT NULL,
    numero VARCHAR(50) DEFAULT NULL,
    valorreceber DECIMAL(10,2) DEFAULT '0.00',
    datavencimento DATE DEFAULT NULL,
    valorrecebido DECIMAL(10,2) DEFAULT '0.00',
    valorjuros DECIMAL(10,2) DEFAULT '0.00',
    valordesconto DECIMAL(10,2) DEFAULT '0.00',
    observacao MEDIUMTEXT,
    numeronf VARCHAR(45) DEFAULT NULL,
    usuarioidusuario INT NOT NULL,
    KEY fk_contasreceber_usuario1_idx (usuarioidusuario),
    CONSTRAINT fk_contasreceber_usuario1
        FOREIGN KEY (usuarioidusuario) REFERENCES usuario (idusuario)
);

CREATE TABLE IF NOT EXISTS contasreceberos (
    contasreceberos INT AUTO_INCREMENT PRIMARY KEY,
    contasreceberidcontasreceber INT NOT NULL,
    ordemservicoidordemServico INT NOT NULL,
    KEY fk_contareceberos_contasreceber1_idx (contasreceberidcontasreceber),
    KEY fk_contareceberos_ordemservico1_idx (ordemservicoidordemServico),
    CONSTRAINT fk_contareceberos_contasreceber1
        FOREIGN KEY (contasreceberidcontasreceber) REFERENCES contasreceber (idcontasreceber),
    CONSTRAINT fk_contareceberos_ordemservico1
        FOREIGN KEY (ordemservicoidordemServico) REFERENCES ordemservico (idordemServico)
);
