# DeskInstaller API

API REST em Spring Boot para gestão de clientes, endereços, ordens de serviço, funcionários e geração de PDF.

## Stack

- Java 17
- Spring Boot 3.2
- Spring Data JPA
- MySQL 8 em produção
- H2 em testes
- Lombok
- Thymeleaf + OpenHTMLtoPDF
- JUnit 5

## Estratégia de execução

O projeto é empacotado como `war` para deploy em um Tomcat externo (10.1 ou superior). O Tomcat embutido está marcado como `provided`, então continua disponível para execução local.

Durante desenvolvimento:

```bash
mvn spring-boot:run
```

Para gerar o pacote de produção:

```bash
mvn clean package
```

O arquivo sai em `target/deskinstaller-api.war`. O passo a passo completo do deploy está em [docs/DEPLOY-PROD.md](docs/DEPLOY-PROD.md).

Atenção ao context path: no modo WAR, o Tomcat monta a aplicação a partir do nome do arquivo. Com `deskinstaller-api.war`, a API responde em `/deskinstaller-api/api/...`, e a propriedade `server.servlet.context-path` é ignorada.

No perfil `dev`, o Flyway fica desligado por padrão para evitar falhas locais com versões mais novas do MySQL.

## Configuração local

O repositório não versiona segredos. O fluxo mais simples em desenvolvimento é usar um arquivo `.env` na raiz do projeto. O Spring Boot está configurado para importar esse arquivo automaticamente.

Passos recomendados:

```bash
cp .env.example .env
```

Depois edite o `.env` com seu usuário e senha do MySQL.

Subida local com Maven:

```bash
mvn spring-boot:run
```

Ou com o script do projeto:

```bash
./start.sh
```

Exemplo de `.env`:

```env
SPRING_PROFILES_ACTIVE=dev
DB_URL=jdbc:mysql://localhost:3306/dk_db?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true
DB_DRIVER=com.mysql.cj.jdbc.Driver
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha
JPA_DDL_AUTO=update
APP_SECURITY_ENABLED=true
APP_SECURITY_PUBLIC_DOCS_ENABLED=true
APP_SECURITY_JWT_SECRET=  # gere com: openssl rand -base64 48
APP_SECURITY_JWT_EXPIRATION_SECONDS=3600
APP_SECURITY_REFRESH_TOKEN_EXPIRATION_SECONDS=604800
```

Observações:

- `.env` fica ignorado no Git
- `.env.example` pode ser versionado com valores de exemplo
- em produção, continue usando variáveis de ambiente reais da plataforma, não um `.env` copiado do desenvolvimento
- em `prod`, a aplicação agora exige segurança habilitada, ao menos um usuário no banco e docs públicas desabilitadas

Exemplo com variáveis de ambiente:

```bash
export DB_URL='jdbc:mysql://localhost:3306/dk_db?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true'
export DB_DRIVER='com.mysql.cj.jdbc.Driver'
export DB_USERNAME='seu_usuario'
export DB_PASSWORD='sua_senha'
export JPA_DDL_AUTO='update'
export APP_CORS_ALLOWED_ORIGINS='http://localhost:3000,http://localhost:4200,http://localhost:5173'
export SPRING_PROFILES_ACTIVE='dev'
```

Se preferir não usar `.env`, também funciona exportar as variáveis diretamente no terminal:

```bash
export DB_URL='jdbc:mysql://localhost:3306/dk_db?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true'
export DB_DRIVER='com.mysql.cj.jdbc.Driver'
export DB_USERNAME='seu_usuario'
export DB_PASSWORD='sua_senha'
export JPA_DDL_AUTO='update'
export APP_CORS_ALLOWED_ORIGINS='http://localhost:3000,http://localhost:4200,http://localhost:5173'
export SPRING_PROFILES_ACTIVE='dev'
```

Outra opção é criar `src/main/resources/application-local.properties` a partir do arquivo de exemplo:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dk_db?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
app.database.monitoring.enabled=false
```

## Perfis

- `dev`: `ddl-auto=update`, monitoramento de banco habilitado, SQL visível e Flyway desligado por padrão
- `prod`: `ddl-auto=validate`, monitoramento desligado, Flyway ligado
- `test`: H2 em memória, segurança desligada, Flyway desligado

Exemplo:

```bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

## Segurança

Com `app.security.enabled=true`, a API exige autenticação via JWT Bearer.

Os usuários ficam persistidos no banco. `APP_SECURITY_USERNAME` e `APP_SECURITY_PASSWORD` não são mais usados para sincronizar um admin na subida da aplicação.

Fluxo:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"SEU_USUARIO_DO_BANCO","password":"SUA_SENHA"}'
```

Depois use o token retornado:

```bash
curl http://localhost:8080/api/clientes \
  -H "Authorization: Bearer SEU_TOKEN"
```

Para renovar a sessão com rotação de refresh token:

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H 'Content-Type: application/json' \
  -d '{"refreshToken":"SEU_REFRESH_TOKEN"}'
```

Para encerrar a sessão:

```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H 'Content-Type: application/json' \
  -d '{"refreshToken":"SEU_REFRESH_TOKEN"}'
```

Endpoints públicos:

- `/`
- `/api/auth/login`
- `/swagger-ui/**`
- `/v3/api-docs/**`

Em `prod`, a recomendação é manter:

```bash
export APP_SECURITY_PUBLIC_DOCS_ENABLED='false'
```

Também defina um segredo JWT próprio e forte:

```bash
export APP_SECURITY_JWT_SECRET="$(openssl rand -base64 48)"
```

O projeto não tem mais nenhum segredo padrão embutido no código. O comportamento é:

- se `APP_SECURITY_JWT_SECRET` estiver vazio em `dev`, a aplicação gera uma chave aleatória válida apenas para aquela execução (os tokens são invalidados a cada restart) e registra um aviso no log
- se estiver vazio em `prod`, a aplicação não sobe
- o segredo precisa ser base64 e ter no mínimo 32 bytes (256 bits) após a decodificação

Em `prod`, a subida também é bloqueada quando:

- o segredo JWT é um dos valores de exemplo que já circularam no repositório
- `app.cors.allowed-origins` está como `*` ou aponta apenas para `localhost`
- `spring.jpa.hibernate.ddl-auto` não é `validate` nem `none`
- a segurança está desligada, não existe usuário no banco, ou as docs estão públicas

## Endpoints principais

### Clientes

- `GET /api/clientes`
- `GET /api/clientes/{id}`
- `POST /api/clientes`
- `PUT /api/clientes/{id}`
- `DELETE /api/clientes/{id}`
- `GET /api/clientes/buscar/nome?q=...`
- `GET /api/clientes/buscar/email?email=...`
- `GET /api/clientes/buscar/telefone?telefone=...`

Compatibilidade legada:

- `POST /api/clientes/salvar`

### Ordens de serviço

- `GET /api/ordens-servico`
- `GET /api/ordens-servico/{id}`
- `POST /api/ordens-servico`
- `PUT /api/ordens-servico/{id}`
- `PATCH /api/ordens-servico/{id}/finalizar`
- `PATCH /api/ordens-servico/{id}/cancelar`
- `DELETE /api/ordens-servico/{id}`
- `GET /api/ordens-servico/ativas`
- `GET /api/ordens-servico/{id}/pdf`

Compatibilidade legada:

- `POST /api/ordemservico/salvar`
- `GET /api/ordemservico/...`

### Financeiro da OS

- `GET /api/os/financeiro/os/{id}`
- `GET /api/os/financeiro/{id}`
- `POST /api/os/financeiro`
- `PUT /api/os/financeiro/{id}`
- `DELETE /api/os/financeiro/{id}`

Compatibilidade legada:

- `GET /api/osfinanceiro/os/{id}`
- `GET /api/osfinanceiro/{id}`
- `POST /api/osfinanceiro`
- `POST /api/osfinanceiro/salvar`
- `DELETE /api/osfinanceiro/deletar/{id}`

## Observabilidade e exposição operacional

Os endpoints de monitoramento de banco ficam desabilitados por padrão:

- `GET /api/database/status`
- `GET /api/database/pool`
- `GET /api/database/test`
- `GET /api/database/tables`

Para habilitar localmente:

```bash
export APP_DATABASE_MONITORING_ENABLED=true
```

## Flyway

O projeto passou a incluir Flyway para migrações incrementais. As migrations atuais cobrem:

- coluna `rgie` em `cliente`
- tabelas de segurança `usuario`, `role` e `usuario_role`
- tabela `refresh_token`
- tabelas operacionais centrais do domínio: `cliente`, `endereco`, `funcionario`, `servico`, `apcliente`, `ordemservico`, `relservico`, `obstecnico`, `osFuncionario` e `osfinanceiro`

Para rodar com Flyway:

```bash
export FLYWAY_ENABLED=true
```

Observação:

- em `prod`, a recomendação é usar `SPRING_PROFILES_ACTIVE=prod`
- o Flyway agora já cobre a base central necessária para subir ambientes novos da API; tabelas periféricas ainda podem exigir migrations adicionais conforme forem sendo ativadas no sistema

## Testes

Os testes usam H2 em memória:

```bash
mvn test
```

Hoje a suíte cobre:

- fluxo CRUD real de clientes
- autenticação obrigatória quando a segurança está ativa
- filtro de aparelhos por cliente e endereço
- validação de email duplicado
- regra de filtro de ordens por `datasituacao`
- fluxo encadeado cliente -> endereço -> aparelho -> ordem -> serviço -> PDF
- testes web dos controllers principais

## Notas de manutenção

- `rgie` agora é persistido na entidade `Cliente`
- o filtro de ordens “ativas ou atualizadas nos últimos 7 dias” usa `datasituacao`
- a API começou a migrar para contratos mais RESTful, mantendo algumas rotas legadas por compatibilidade
- a segurança agora é centralizada via Spring Security
- o login JWT agora autentica contra usuários persistidos no banco
- na subida com segurança habilitada, o sistema sincroniza o usuário admin do `.env` no banco
- os papéis base são `ADMIN`, `ATENDENTE`, `TECNICO` e `FINANCEIRO`
- o projeto tem perfis `dev` e `prod`, além de suporte inicial a Flyway

## Arquivos úteis

- [DEPLOY-PROD.md](docs/DEPLOY-PROD.md)
- [CONFIG-MYSQL.md](docs/CONFIG-MYSQL.md)
- [MIGRACAO-JPA.md](docs/MIGRACAO-JPA.md)
- [PDF-GENERATOR-README.md](docs/PDF-GENERATOR-README.md)
- [OSPDF-SERVICE-README.md](docs/OSPDF-SERVICE-README.md)
