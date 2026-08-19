CREATE TABLE IF NOT EXISTS cliente (
    idcliente INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    tipoPessoa VARCHAR(50) NOT NULL,
    dataNascimento DATE NOT NULL,
    foneResidencial VARCHAR(50),
    foneCelular VARCHAR(50),
    foneComercial VARCHAR(50),
    email VARCHAR(255),
    contato VARCHAR(255),
    cpfcnpj VARCHAR(50),
    rgie VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS endereco (
    idendereco INT AUTO_INCREMENT PRIMARY KEY,
    tipoLogradouro VARCHAR(100),
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(50),
    complemento VARCHAR(255),
    bairro VARCHAR(255),
    cep VARCHAR(50),
    cidade VARCHAR(255) NOT NULL,
    estado VARCHAR(50),
    pontoReferencia VARCHAR(255),
    foneInstalacao VARCHAR(50),
    idmaps VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT FALSE,
    cliente_idcliente INT NOT NULL,
    CONSTRAINT fk_endereco_cliente
        FOREIGN KEY (cliente_idcliente) REFERENCES cliente (idcliente)
);

CREATE TABLE IF NOT EXISTS funcionario (
    idfuncionario INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255),
    foneCelular VARCHAR(50),
    valorComissao FLOAT,
    funcao VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS servico (
    idservico INT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    situacao BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS apcliente (
    idapCliente INT AUTO_INCREMENT PRIMARY KEY,
    dataCompra DATE,
    notaFiscal VARCHAR(255),
    loja VARCHAR(255),
    dataInstalacao DATE,
    dataManutencao DATE,
    local VARCHAR(255),
    cliente_idcliente INT NOT NULL,
    endereco_idendereco INT,
    modelo VARCHAR(255),
    fabricante VARCHAR(255),
    modeloEvaporadora VARCHAR(255),
    nsEvaporadora VARCHAR(255),
    modeloCodensadora VARCHAR(255),
    nsCodensadora VARCHAR(255),
    capacidade VARCHAR(255),
    dataultimamanutencao DATE,
    ativo BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_apcliente_cliente
        FOREIGN KEY (cliente_idcliente) REFERENCES cliente (idcliente),
    CONSTRAINT fk_apcliente_endereco
        FOREIGN KEY (endereco_idendereco) REFERENCES endereco (idendereco)
);

CREATE TABLE IF NOT EXISTS ordemservico (
    idordemServico INT AUTO_INCREMENT PRIMARY KEY,
    horaServico VARCHAR(50) NOT NULL,
    dataServico DATE NOT NULL,
    valor DOUBLE NOT NULL,
    observacao MEDIUMTEXT,
    situacao VARCHAR(100),
    datasituacao DATE,
    valorComissao DOUBLE,
    cliente_idcliente INT,
    endereco_idendereco INT,
    indicacao VARCHAR(255),
    recebida BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_ordemservico_cliente
        FOREIGN KEY (cliente_idcliente) REFERENCES cliente (idcliente),
    CONSTRAINT fk_ordemservico_endereco
        FOREIGN KEY (endereco_idendereco) REFERENCES endereco (idendereco)
);

CREATE TABLE IF NOT EXISTS relservico (
    idrelServico INT AUTO_INCREMENT PRIMARY KEY,
    descricao MEDIUMTEXT,
    quantidade DOUBLE NOT NULL,
    valor DOUBLE NOT NULL,
    servico_idservico INT NOT NULL,
    ordemServico_idordemServico INT,
    apcliente_idapCliente INT NOT NULL,
    situacao BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_relservico_servico
        FOREIGN KEY (servico_idservico) REFERENCES servico (idservico),
    CONSTRAINT fk_relservico_ordemservico
        FOREIGN KEY (ordemServico_idordemServico) REFERENCES ordemservico (idordemServico),
    CONSTRAINT fk_relservico_apcliente
        FOREIGN KEY (apcliente_idapCliente) REFERENCES apcliente (idapCliente)
);

CREATE TABLE IF NOT EXISTS obstecnico (
    idobsTecnico INT AUTO_INCREMENT PRIMARY KEY,
    observacao MEDIUMTEXT,
    funcionario_idfuncionario INT NOT NULL,
    ordemservico_idordemservico INT,
    datahora TIMESTAMP NULL,
    ativo BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_obstecnico_funcionario
        FOREIGN KEY (funcionario_idfuncionario) REFERENCES funcionario (idfuncionario),
    CONSTRAINT fk_obstecnico_ordemservico
        FOREIGN KEY (ordemservico_idordemservico) REFERENCES ordemservico (idordemServico)
);

CREATE TABLE IF NOT EXISTS osfuncionario (
    idosFuncionario INT AUTO_INCREMENT PRIMARY KEY,
    ordemservico_idordemservico INT,
    funcionario_idfuncionario INT NOT NULL,
    CONSTRAINT fk_osfuncionario_ordemservico
        FOREIGN KEY (ordemservico_idordemservico) REFERENCES ordemservico (idordemServico),
    CONSTRAINT fk_osfuncionario_funcionario
        FOREIGN KEY (funcionario_idfuncionario) REFERENCES funcionario (idfuncionario)
);

CREATE TABLE IF NOT EXISTS osfinanceiro (
    idosfinanceiro INT AUTO_INCREMENT PRIMARY KEY,
    data DATE,
    parcelas INT NOT NULL DEFAULT 0,
    valordesconto FLOAT,
    valorrecebido FLOAT,
    formapagamento VARCHAR(100),
    ordemservico_idordemservico INT NOT NULL,
    CONSTRAINT fk_osfinanceiro_ordemservico
        FOREIGN KEY (ordemservico_idordemservico) REFERENCES ordemservico (idordemServico)
);
