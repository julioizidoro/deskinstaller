# Migração para Spring Data JPA com Jakarta Persistence

## ✅ Status do Projeto

**Todas as classes do modelo foram migradas com sucesso para Jakarta Persistence API (JPA 3.0+)**

- ✅ 27 entidades JPA corrigidas e compiladas
- ✅ Spring Data JPA configurado
- ✅ H2 Database in-memory funcionando
- ✅ API REST implementada
- ✅ Aplicação rodando sem erros

---

## 🔄 Mudanças Principais

### 1. Migração de Imports

**ANTES (Java EE / javax.persistence)**
```java
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
```

**DEPOIS (Jakarta EE / jakarta.persistence)**
```java
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
```

### 2. Correção de Packages

**ANTES**
```java
package model;
```

**DEPOIS**
```java
package br.com.deskinstaller.model;
```

### 3. Spring Data JPA Repository Pattern

Exemplo de repository criado:
```java
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByWinnerTrue();
}
```

---

## 📦 Entidades JPA Disponíveis

### Package: `br.com.deskinstaller.model`

| Entidade | Tabela | Descrição |
|----------|--------|-----------|
| `Apcliente` | apcliente | Aparelhos de clientes |
| `Banco` | banco | Dados bancários |
| `Cliente` | cliente | Cadastro de clientes |
| `ComissaoFuncionario` | - | Comissões |
| `Contaspagar` | contaspagar | Contas a pagar |
| `Controlecheques` | controlecheques | Controle de cheques |
| `Empresa` | empresa | Dados da empresa |
| `Endereco` | endereco | Endereços |
| `Formacontaspagar` | formacontaspagar | Formas de pagamento |
| `Funcao` | funcao | Funções |
| `Funcionario` | funcionario | Funcionários |
| `Grupoconta` | grupoconta | Grupos de contas |
| `Loja` | loja | Lojas |
| `Movimentocaixa` | movimentocaixa | Caixa |
| `Obstecnico` | obstecnico | Obs. técnicas |
| `Orcamento` | orcamento | Orçamentos |
| `Ordemservico` | ordemservico | OS |
| `OsFuncionario` | osFuncionario | OS x Funcionário |
| `Pagamentocontaspagar` | pagamentocontaspagar | Pagamentos |
| `Parametros` | parametros | Parâmetros |
| `Planoconta` | planoconta | Plano de contas |
| `Relorcamento` | relorcamento | Rel. orçamento |
| `Relservico` | relservico | Rel. serviço |
| `Servico` | servico | Serviços |
| `Subgrupo` | subgrupo | Subgrupos |
| `Vendedor` | vendedor | Vendedores |

### Package: `com.avaliacao.model`

| Entidade | Tabela | Descrição |
|----------|--------|-----------|
| `Movie` | movies | Filmes do Golden Raspberry Awards |

---

## 🚀 Como Usar

### Iniciar a Aplicação

```bash
# Porta padrão 8080
mvn spring-boot:run

# Porta customizada
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Testar a API

```bash
# Executar script de testes
./test-api.sh

# Ou porta customizada
./test-api.sh 8081
```

### Endpoints Disponíveis

```http
GET    /api/movies                        # Lista todos os filmes
GET    /api/movies/{id}                   # Busca filme por ID
GET    /api/movies/producers/intervals    # Intervalos min/max
POST   /api/movies/load                   # Upload CSV
```

---

## 🗃️ Configuração do Banco de Dados

### application.properties

```properties
# H2 Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Console H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### Acessar Console H2

1. Abra: http://localhost:8080/h2-console
2. JDBC URL: `jdbc:h2:mem:testdb`
3. User: `sa`
4. Password: _(deixe em branco)_

---

## 🛠️ Criar Novos Repositories

```java
package br.com.deskinstaller.repository;

import br.com.deskinstaller.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    
    // Query methods automáticos do Spring Data JPA
    List<Cliente> findByNomeContaining(String nome);
    
    Cliente findByEmail(String email);
    
    List<Cliente> findByDataNascimentoBetween(Date inicio, Date fim);
    
    // Query customizada
    @Query("SELECT c FROM Cliente c WHERE c.nome LIKE %:termo%")
    List<Cliente> buscarPorTermo(@Param("termo") String termo);
}
```

---

## 📝 Exemplo de Service

```java
package br.com.deskinstaller.service;

import br.com.deskinstaller.model.Cliente;
import br.com.deskinstaller.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    public List<Cliente> listarTodos() {
        return repository.findAll();
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Cliente salvar(Cliente cliente) {
        return repository.save(cliente);
    }

    @Transactional
    public void deletar(Integer id) {
        repository.deleteById(id);
    }

    public List<Cliente> buscarPorNome(String nome) {
        return repository.findByNomeContaining(nome);
    }
}
```

---

## 🎯 Próximos Passos

1. ✅ **Migração JPA concluída**
2. ⏭️ Criar repositories para as demais entidades
3. ⏭️ Criar services com lógica de negócio
4. ⏭️ Criar controllers REST
5. ⏭️ Adicionar validações (@Valid, Bean Validation)
6. ⏭️ Configurar relacionamentos JPA (@OneToMany, @ManyToOne)
7. ⏭️ Implementar testes unitários e de integração
8. ⏭️ Adicionar segurança (Spring Security)

---

## 📚 Referências

- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Jakarta Persistence](https://jakarta.ee/specifications/persistence/)
- [Spring Boot 3.x Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)
- [H2 Database](https://www.h2database.com/)

---

## ⚠️ Notas Importantes

- **Spring Boot 3.x** requer **Java 17+**
- **Jakarta EE 9+** substitui **Java EE 8** (javax → jakarta)
- Todas as anotações JPA foram atualizadas
- O projeto compila sem erros
- A aplicação está rodando na porta **8081**

---

**Desenvolvido com ❤️ usando Spring Boot 3.2.0 + Jakarta Persistence 3.0**

