# Deploy em produção — WAR no Tomcat externo

Guia para publicar a DeskInstaller API em um Tomcat já instalado, com MySQL no mesmo servidor.

## 1. Pré-requisitos do servidor

Estes dois itens são eliminatórios. Confira antes de qualquer coisa:

| Requisito | Versão mínima | Como verificar |
|---|---|---|
| **Tomcat** | **10.1+** | `$CATALINA_HOME/bin/version.sh` |
| **Java** | **17+** | `java -version` |
| MySQL | 8.0 | `mysql --version` |

> **Atenção ao Tomcat 9.** O Spring Boot 3.2 usa o namespace `jakarta.*` (Jakarta EE 10). O Tomcat 9 usa `javax.*` e **não consegue** rodar esta aplicação — o deploy falha com `ClassNotFoundException` em classes `jakarta.servlet`. Se o seu Tomcat for 9.x, é obrigatório migrar para 10.1+ antes de seguir.

## 2. O que mudou no projeto

Para permitir o deploy em Tomcat externo:

- `pom.xml`: `<packaging>` passou de `jar` para `war`
- `pom.xml`: `spring-boot-starter-tomcat` marcado como `provided` — o container agora é fornecido pelo Tomcat do servidor, não embutido no pacote
- `pom.xml`: `<finalName>deskinstaller-api</finalName>` — gera o arquivo sem a versão no nome
- nova classe `ServletInitializer` — ponto de entrada exigido pelo Tomcat

O desenvolvimento local **não muda**: `mvn spring-boot:run` continua funcionando normalmente.

## 3. Context path

No modo WAR, o Tomcat define o caminho da aplicação pelo **nome do arquivo**, e a propriedade `server.servlet.context-path` do `application.properties` passa a ser ignorada.

| Arquivo publicado | URL base da API |
|---|---|
| `deskinstaller-api.war` | `https://seu-dominio/deskinstaller-api/api/...` |
| `ROOT.war` | `https://seu-dominio/api/...` |

Se quiser a API na raiz do domínio, renomeie o arquivo para `ROOT.war` ao copiar — mas isso substitui a aplicação padrão do Tomcat.

**Isto afeta o front-end:** a URL base dos clientes muda. Ajuste antes de publicar.

## 4. Gerar o pacote

```bash
mvn clean package -DskipTests
```

Gera `target/deskinstaller-api.war`.

Recomendado rodar a suíte antes de publicar:

```bash
mvn clean verify
```

## 5. Configuração por variáveis de ambiente

O arquivo `.env` **não funciona no Tomcat** — ele é lido a partir do diretório de trabalho do processo, que passa a ser `$CATALINA_HOME/bin`. Use o `setenv.sh`.

Crie `$CATALINA_HOME/bin/setenv.sh`:

```bash
#!/bin/bash

export SPRING_PROFILES_ACTIVE='prod'

export DB_URL='jdbc:mysql://localhost:3306/dk_db?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true'
export DB_DRIVER='com.mysql.cj.jdbc.Driver'
export DB_USERNAME='usuario_da_api'
export DB_PASSWORD='SENHA_FORTE_AQUI'

export JPA_DDL_AUTO='validate'

export APP_SECURITY_ENABLED='true'
export APP_SECURITY_PUBLIC_DOCS_ENABLED='false'
export APP_SECURITY_JWT_SECRET='COLE_AQUI_O_SEGREDO'
export APP_SECURITY_JWT_EXPIRATION_SECONDS='3600'
export APP_SECURITY_REFRESH_TOKEN_EXPIRATION_SECONDS='604800'

export APP_CORS_ALLOWED_ORIGINS='https://seu-front.com.br'

export APP_DATABASE_MONITORING_ENABLED='false'
export FLYWAY_ENABLED='false'
```

Proteja o arquivo, que contém senha e segredo:

```bash
chmod 700 $CATALINA_HOME/bin/setenv.sh
chown tomcat:tomcat $CATALINA_HOME/bin/setenv.sh
```

Gere o segredo JWT com:

```bash
openssl rand -base64 48
```

## 6. Barreiras de subida no perfil prod

Com `SPRING_PROFILES_ACTIVE=prod`, a aplicação **se recusa a subir** se alguma destas condições não for atendida. Isso é proposital: é melhor falhar no deploy do que subir exposta.

| Verificação | Exigência |
|---|---|
| Segurança | `APP_SECURITY_ENABLED=true` |
| Usuários | ao menos um registro na tabela `usuario` |
| Documentação | `APP_SECURITY_PUBLIC_DOCS_ENABLED=false` |
| Segredo JWT | definido, com 32+ caracteres, e diferente dos exemplos do repositório |
| CORS | não pode ser `*` nem apontar apenas para `localhost` |
| Schema | `JPA_DDL_AUTO` em `validate` ou `none` |

## 7. Banco de dados

Crie um usuário MySQL dedicado, sem privilégios de administrador:

```sql
CREATE USER 'usuario_da_api'@'localhost' IDENTIFIED BY 'SENHA_FORTE_AQUI';
GRANT SELECT, INSERT, UPDATE, DELETE ON dk_db.* TO 'usuario_da_api'@'localhost';
FLUSH PRIVILEGES;
```

Como o banco fica na mesma máquina, restrinja o acesso a `localhost` e mantenha a porta 3306 fechada no firewall.

**Sobre o Flyway:** o guia sugere `FLYWAY_ENABLED=false` na primeira subida porque o `dk_db` é um banco legado que já possui as tabelas. Ligar o Flyway sem preparar a linha de base pode fazê-lo tentar recriar o que já existe. Quando for adotá-lo, faça o baseline primeiro em uma cópia do banco e valide.

## 8. Publicar

```bash
sudo systemctl stop tomcat
sudo cp target/deskinstaller-api.war $CATALINA_HOME/webapps/
sudo systemctl start tomcat
sudo tail -f $CATALINA_HOME/logs/catalina.out
```

Aguarde a linha `Started DeskInstallerApplication`.

## 9. Verificar

```bash
curl -i -X POST https://seu-dominio/deskinstaller-api/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"SEU_USUARIO","password":"SUA_SENHA"}'
```

Checklist do que deve acontecer:

- login retorna `200` com `accessToken`
- `GET /api/clientes` **sem** token retorna `401`
- `GET /swagger-ui.html` retorna `404` ou `401` (documentação fechada em produção)

## 10. HTTPS

A API trafega tokens JWT, então HTTPS não é opcional. O caminho mais simples é colocar um nginx na frente do Tomcat, terminando o TLS com certificado Let's Encrypt, e deixar o Tomcat escutando apenas em `127.0.0.1:8080`.

Se usar proxy reverso, informe ao Tomcat para respeitar os cabeçalhos de origem, senão a aplicação gera URLs `http://` e a validação de HSTS não funciona:

```
server.forward-headers-strategy=NATIVE
```

## 11. Antes de publicar — pendências conhecidas

- [ ] **Trocar a senha do MySQL** que vazou no histórico do Git (`.env.example`, commits `608fed9`, `2f3189c`, `90989d6`)
- [ ] Gerar um segredo JWT novo, nunca reaproveitar o de desenvolvimento
- [ ] Confirmar que o Tomcat do servidor é 10.1+
- [ ] Criar o usuário administrador com senha forte (não `admin/admin`)
- [ ] Ajustar a URL base no front-end por causa do context path
- [ ] Definir rotina de backup do `dk_db`
