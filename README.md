# 🏢 DeskInstaller - Sistema de Gestão

Sistema completo de gestão desenvolvido com **Spring Boot 3** + **MySQL** + **Lombok** para gerenciamento de clientes, ordens de serviço, funcionários e operações da empresa DeskInstaller.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 🚀 Tecnologias

- ☕ **Java 17**
- 🍃 **Spring Boot 3.2.0**
- 🗄️ **MySQL 8.0+**
- 📊 **Spring Data JPA**
- 🔄 **Jakarta Persistence (JPA 3.0+)**
- 🎯 **Lombok** - Redução de boilerplate
- 🏊 **HikariCP** - Pool de conexões
- 📖 **SpringDoc OpenAPI** - Documentação da API
- 🧪 **JUnit 5** - Testes

---

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.8+
- MySQL 8.0+
- Git

---

## 🔧 Instalação

### 1. Clonar o repositório

```bash
git clone https://github.com/julioizidoro/deskinstaller.git
cd deskinstaller
```

### 2. Configurar o Banco de Dados

```bash
# Executar script SQL de configuração
mysql -u root -p < setup-database.sql
```

Ou manualmente:

```sql
CREATE DATABASE dk_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'julioizidoro'@'localhost' IDENTIFIED BY 'sua_senha';
GRANT ALL PRIVILEGES ON dk_db.* TO 'julioizidoro'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configurar application.properties

Edite `src/main/resources/application.properties` se necessário:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dk_db
spring.datasource.username=julioizidoro
spring.datasource.password=sua_senha
```

### 4. Compilar o projeto

```bash
mvn clean package -DskipTests
```

### 5. Executar a aplicação

```bash
mvn spring-boot:run
```

Ou:

```bash
java -jar target/DeskInstaller-1.0-SNAPSHOT.jar
```

---

## 🌐 Endpoints da API

### Base URL
```
http://localhost:8080
```

### Clientes

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/clientes` | Lista todos os clientes |
| GET | `/api/clientes/{id}` | Busca cliente por ID |
| GET | `/api/clientes/buscar/nome?q=` | Busca por nome |
| GET | `/api/clientes/buscar/email?email=` | Busca por email |
| GET | `/api/clientes/buscar/telefone?telefone=` | Busca por telefone |
| POST | `/api/clientes` | Cria novo cliente |
| PUT | `/api/clientes/{id}` | Atualiza cliente |
| DELETE | `/api/clientes/{id}` | Remove cliente |
| GET | `/api/clientes/count` | Conta total |

### Monitoramento de Banco de Dados

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/database/status` | Status da conexão |
| GET | `/api/database/pool` | Info do pool de conexões |
| GET | `/api/database/test` | Testa conexão |
| GET | `/api/database/tables` | Lista tabelas |

### Documentação

```
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
```

---

## 📊 Modelo de Dados

### Principais Entidades

- **Cliente** - Cadastro de clientes
- **Endereco** - Endereços dos clientes
- **Funcionario** - Funcionários da empresa
- **Ordemservico** - Ordens de serviço
- **Orcamento** - Orçamentos
- **Apcliente** - Aparelhos dos clientes
- **Loja** - Lojas da empresa
- **Vendedor** - Vendedores
- **Banco** - Dados bancários
- **Contaspagar** - Contas a pagar
- E mais 16 entidades...

**Total:** 26+ entidades JPA

---

## 🧪 Testes

```bash
# Executar testes
mvn test

# Testes de integração
mvn verify

# Com cobertura
mvn clean test jacoco:report
```

---

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/br/com/deskinstaller/
│   │   ├── DeskInstallerApplication.java
│   │   ├── config/
│   │   │   ├── DatabaseConfig.java
│   │   │   └── DatabaseConnectionValidator.java
│   │   ├── controller/
│   │   │   ├── ClienteController.java
│   │   │   └── DatabaseMonitorController.java
│   │   ├── dto/
│   │   │   └── ClienteDTO.java
│   │   ├── model/
│   │   │   ├── Cliente.java
│   │   │   └── ... (26 entidades)
│   │   ├── repository/
│   │   │   └── ClienteRepository.java
│   │   └── service/
│   │       └── ClienteService.java
│   └── resources/
│       ├── application.properties
│       └── logback-spring.xml
└── test/
    └── java/
        └── ...
```

---

## 🔒 Segurança

⚠️ **Importante:** Não commite senhas no repositório!

Use variáveis de ambiente:

```bash
export DB_PASSWORD='sua_senha_aqui'
mvn spring-boot:run
```

Ou crie `application-local.properties` (já ignorado pelo .gitignore):

```properties
spring.datasource.password=${DB_PASSWORD}
```

---

## 📖 Documentação Adicional

- [CONFIG-MYSQL.md](CONFIG-MYSQL.md) - Configuração detalhada do MySQL
- [API-CLIENTES.md](API-CLIENTES.md) - Documentação completa da API de Clientes
- [MIGRACAO-JPA.md](MIGRACAO-JPA.md) - Guia de migração Jakarta Persistence

---

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/NovaFuncionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/NovaFuncionalidade`)
5. Abra um Pull Request

---

## 📝 Roadmap

- [ ] Implementar autenticação JWT
- [ ] Adicionar cache com Redis
- [ ] Criar dashboard administrativo
- [ ] Implementar notificações em tempo real
- [ ] Deploy em Docker/Kubernetes
- [ ] CI/CD com GitHub Actions

---

## 👨‍💻 Autor

**Julio Izidoro**

- GitHub: [@julioizidoro](https://github.com/julioizidoro)
- LinkedIn: [Julio Izidoro](https://linkedin.com/in/julioizidoro)

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 🙏 Agradecimentos

- Spring Team pela excelente framework
- Comunidade open-source
- Todos os contribuidores

---

**Desenvolvido com ❤️ usando Spring Boot 3 + MySQL + Lombok**

**Data:** 13 de Novembro de 2025

