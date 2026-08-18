# 🔧 Como Resolver Erros de Dependências no PdfGeneratorService

## ❌ Problema

A IDE (IntelliJ/Eclipse) não está reconhecendo as dependências:
- `com.openhtmltopdf` (OpenHTMLtoPDF)
- `org.thymeleaf` (Thymeleaf)

**Sintomas:**
- Linhas vermelhas nos imports
- "Cannot resolve symbol 'openhtmltopdf'"
- "Cannot resolve symbol 'thymeleaf'"

---

## ✅ Solução

### Opção 1: Recarregar Dependências Maven (RECOMENDADO)

#### No IntelliJ IDEA:

1. **Clique com botão direito** no arquivo `pom.xml`
2. Selecione: **Maven** → **Reload Project**
3. Aguarde o download das dependências (veja na barra inferior)
4. Se não funcionar, tente:
   - **File** → **Invalidate Caches** → **Restart**

#### No Eclipse:

1. **Clique com botão direito** no projeto
2. Selecione: **Maven** → **Update Project**
3. Marque **Force Update of Snapshots/Releases**
4. Clique **OK**

#### Via Linha de Comando:

```bash
cd /Users/julioizidoro/Git/deskInstalller-api

# Limpar e baixar dependências
mvn clean install -DskipTests

# Se der erro, force o download
mvn dependency:purge-local-repository
mvn clean install -DskipTests
```

---

### Opção 2: Verificar se o Projeto Compila (Ignorar Erros da IDE)

Mesmo que a IDE mostre erros, o Maven pode compilar corretamente:

```bash
cd /Users/julioizidoro/Git/deskInstalller-api

# Testar compilação
mvn clean compile

# Se compilar sem erros, rode a aplicação
mvn spring-boot:run
```

**Se compilar com sucesso**, os erros são apenas da IDE (cache) e você pode ignorá-los.

---

### Opção 3: Reinstalar Dependências

```bash
# 1. Parar a aplicação se estiver rodando
pkill -9 -f spring-boot

# 2. Limpar cache Maven
rm -rf ~/.m2/repository/com/openhtmltopdf
rm -rf ~/.m2/repository/org/thymeleaf

# 3. Baixar novamente
cd /Users/julioizidoro/Git/deskInstalller-api
mvn clean install -U
```

---

## 🧪 Testar Se Está Funcionando

### 1. Compilar e Rodar:

```bash
cd /Users/julioizidoro/Git/deskInstalller-api
mvn clean spring-boot:run
```

### 2. Testar o Endpoint:

```bash
# Visualizar HTML (deve funcionar)
curl http://localhost:8080/os/1/visualizar

# Gerar PDF (deve funcionar se compilou)
curl -o teste.pdf http://localhost:8080/os/1/pdf

# Verificar se PDF foi criado
ls -lh teste.pdf
```

---

## 🔍 Verificar Dependências

### Comando para listar dependências baixadas:

```bash
mvn dependency:tree | grep -E "thymeleaf|openhtmltopdf"
```

**Saída esperada:**
```
[INFO] +- org.springframework.boot:spring-boot-starter-thymeleaf:jar:3.2.0
[INFO] |  +- org.thymeleaf:thymeleaf-spring6:jar:3.1.2.RELEASE
[INFO] +- com.openhtmltopdf:openhtmltopdf-pdfbox:jar:1.0.10
[INFO] +- com.openhtmltopdf:openhtmltopdf-slf4j:jar:1.0.10
```

Se aparecer, as dependências estão OK.

---

## 📋 Checklist de Verificação

Execute estes comandos e anote os resultados:

```bash
# 1. Verificar se pom.xml tem as dependências
grep -A3 "thymeleaf\|openhtmltopdf" pom.xml

# 2. Tentar compilar
mvn clean compile 2>&1 | grep -E "BUILD SUCCESS|BUILD FAILURE"

# 3. Se compilar, rodar aplicação
mvn spring-boot:run
```

---

## ⚠️ Se NADA Funcionar

### Último Recurso: Usar Biblioteca Alternativa

Se as dependências não baixarem, podemos usar **iText** ou **Flying Saucer** como alternativa:

```xml
<!-- Alternativa: Flying Saucer (mais simples) -->
<dependency>
    <groupId>org.xhtmlrenderer</groupId>
    <artifactId>flying-saucer-pdf</artifactId>
    <version>9.1.22</version>
</dependency>
```

---

## 🎯 Resposta Rápida

**Se você quer apenas TESTAR se funciona:**

```bash
# Ignore os erros da IDE e rode:
cd /Users/julioizidoro/Git/deskInstalller-api
mvn clean spring-boot:run

# Depois acesse:
http://localhost:8080/os/1/pdf
```

Se o PDF baixar, **ESTÁ FUNCIONANDO!** Os erros são apenas da IDE.

---

## 💡 Por Que Isso Acontece?

### Causas Comuns:

1. **Cache da IDE desatualizado** - IntelliJ/Eclipse não recarregou o pom.xml
2. **Maven local corrompido** - Dependências baixadas pela metade
3. **Proxy/Firewall** - Bloqueando download do repositório Maven Central
4. **Versão Java incompatível** - Projeto usa Java 17

### Como Evitar:

- Sempre fazer **Maven → Reload Project** após editar `pom.xml`
- Usar `mvn clean` antes de compilar
- Manter IDE atualizada

---

## ✅ Resultado Esperado

Após seguir os passos acima:

✅ IDE não mostra mais erros vermelhos  
✅ `mvn clean compile` executa com sucesso  
✅ `mvn spring-boot:run` inicia sem erros  
✅ `http://localhost:8080/os/1/pdf` baixa o PDF

---

**🚀 Tente primeiro a Opção 1 (Reload Maven). Se não resolver em 5 minutos, vá direto para Opção 2 (testar se compila)!**

