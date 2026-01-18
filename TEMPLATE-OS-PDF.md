# Template Thymeleaf para Ordem de Serviço (PDF)

## 📋 Resumo da Validação e Correções

✅ **HTML Thymeleaf validado e corrigido** para geração de PDF

### Problemas Corrigidos

1. ✅ **CSS melhorado**: Adicionado `CDATA` e estilos robustos para PDF
2. ✅ **Flexbox removido**: Substituído por tabelas (melhor compatibilidade com geradores PDF)
3. ✅ **Proteção contra nulos**: Adicionado operador Elvis `?: '-'` em todos os campos
4. ✅ **Tag span mal formatada**: Corrigido quebra de linha no th:text
5. ✅ **Formatação condicional**: Adicionado `th:if` para campos opcionais (evaporadora/condensadora)
6. ✅ **Estrutura de tabelas**: Layout mais consistente usando `info-table`

---

## 📁 Arquivos Criados/Modificados

### Templates
- ✅ `src/main/resources/templates/OsHTML.html` - Template Thymeleaf corrigido

### DTOs
- ✅ `src/main/java/br/com/deskinstaller/dto/OsDTO.java` - DTO para dados da OS

### Controllers
- ✅ `src/main/java/br/com/deskinstaller/controller/OsPDFController.java` - Controller de exemplo

---

## 🚀 Como Testar

### 1. Rodar a aplicação
```bash
mvn spring-boot:run
```

### 2. Acessar no navegador
```
http://localhost:8080/os/5305/visualizar
```

Você verá o HTML renderizado com dados de exemplo (mock).

---

## 🔧 Como Usar na Produção

### Passo 1: Criar um Service
```java
@Service
public class OrdemServicoService {
    
    public OsDTO buscarOsParaPDF(Integer idOrdemServico) {
        // 1. Buscar ordem de serviço do banco
        Ordemservico os = ordemServicoRepository.findById(idOrdemServico)
            .orElseThrow(() -> new RuntimeException("OS não encontrada"));
        
        // 2. Buscar cliente, endereço, serviços relacionados
        Cliente cliente = clienteRepository.findById(os.getCliente()).orElse(null);
        Endereco endereco = enderecoRepository.findById(os.getEndereco()).orElse(null);
        List<Relservico> servicos = relServicoRepository.findByOrdemservico(idOrdemServico);
        
        // 3. Converter para DTO
        return converterParaOsDTO(os, cliente, endereco, servicos);
    }
    
    private OsDTO converterParaOsDTO(Ordemservico os, Cliente cliente, 
                                      Endereco endereco, List<Relservico> servicos) {
        return OsDTO.builder()
            .numero(os.getIdordemServico().toString())
            .endereco(endereco != null ? endereco.getRua() : "-")
            .bairro(endereco != null ? endereco.getBairro() : "-")
            .cidade(endereco != null ? endereco.getCidade() : "-")
            .clienteNome(cliente != null ? cliente.getNome() : "-")
            // ... mapear todos os campos
            .servicos(servicos.stream()
                .map(this::converterServicoDTO)
                .collect(Collectors.toList()))
            .total(formatarValor(os.getValor()))
            .build();
    }
}
```

### Passo 2: Atualizar o Controller
```java
@Controller
@RequiredArgsConstructor
public class OsPDFController {
    
    private final OrdemServicoService ordemServicoService;
    
    @GetMapping("/{id}/visualizar")
    public String visualizarOS(@PathVariable Integer id, Model model) {
        OsDTO osDTO = ordemServicoService.buscarOsParaPDF(id);
        model.addAttribute("os", osDTO);
        return "OsHTML";
    }
}
```

---

## 📄 Geração de PDF

### Opção 1: Flying Saucer (Recomendado para Thymeleaf)

#### Adicionar dependência no `pom.xml`:
```xml
<dependency>
    <groupId>org.xhtmlrenderer</groupId>
    <artifactId>flying-saucer-pdf</artifactId>
    <version>9.1.22</version>
</dependency>
```

#### Service para gerar PDF:
```java
@Service
@RequiredArgsConstructor
public class PdfService {
    
    private final SpringTemplateEngine templateEngine;
    
    public byte[] gerarPdfOrdemServico(Integer idOrdemServico) {
        // 1. Buscar dados
        OsDTO osDTO = ordemServicoService.buscarOsParaPDF(idOrdemServico);
        
        // 2. Renderizar HTML com Thymeleaf
        Context context = new Context();
        context.setVariable("os", osDTO);
        String html = templateEngine.process("OsHTML", context);
        
        // 3. Converter HTML para PDF
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }
}
```

#### Controller para download:
```java
@GetMapping("/{id}/pdf")
public ResponseEntity<byte[]> gerarPDF(@PathVariable Integer id) {
    byte[] pdf = pdfService.gerarPdfOrdemServico(id);
    
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDisposition(
        ContentDisposition.attachment()
            .filename("OS-" + id + ".pdf")
            .build()
    );
    
    return ResponseEntity.ok()
        .headers(headers)
        .body(pdf);
}
```

---

## 🎨 Customização do Template

### Alterar estilos
Edite a seção `<style>` em `OsHTML.html`:
```html
<style>
    /*<![CDATA[*/
    body { 
        font-family: 'Times New Roman', serif; /* trocar fonte */
        font-size: 10px; /* alterar tamanho */
    }
    /*]]>*/
</style>
```

### Adicionar campos
1. Adicione o campo no `OsDTO.java`
2. Use `th:text` no template:
```html
<td class="info-label">Novo Campo:</td>
<td th:text="${os.novoCampo} ?: '-'">valor padrão</td>
```

### Formatação de valores

#### Dinheiro:
```html
<!-- No template -->
<span th:text="${#numbers.formatDecimal(os.valor, 1, 'POINT', 2, 'COMMA')}">250,00</span>

<!-- Ou pré-formatar no DTO (recomendado) -->
private String valor = String.format("%.2f", valorNumerico);
```

#### Datas:
```html
<!-- No template -->
<span th:text="${#temporals.format(os.dataServico, 'dd/MM/yyyy')}">30/09/2024</span>

<!-- Ou pré-formatar no DTO (recomendado) -->
private String data = dataServico.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
```

---

## ⚠️ Observações Importantes

### Para Geração de PDF

1. **CSS Simplificado**: Geradores de PDF têm suporte limitado a CSS moderno
   - ❌ Evite: flexbox, grid, position absolute/fixed
   - ✅ Use: tabelas, floats, margin/padding simples

2. **Imagens**: Se adicionar logo/imagens, use caminho absoluto ou base64
   ```html
   <img th:src="@{/static/logo.png}" alt="Logo"/>
   ```

3. **Fontes**: Para fontes customizadas, inclua `@font-face` no CSS

4. **Tamanho de Página**: Adicione no CSS se necessário
   ```css
   @page {
       size: A4;
       margin: 2cm;
   }
   ```

---

## 🧪 Testes

### Validar sintaxe Thymeleaf
O template já está validado com:
- ✅ Namespace correto: `xmlns:th="http://www.thymeleaf.org"`
- ✅ Proteção contra nulos: operador Elvis `?:`
- ✅ Loops: `th:each` para lista de serviços
- ✅ Condicionais: `th:if` para campos opcionais

### Testar localmente
```bash
# 1. Rodar aplicação
mvn spring-boot:run

# 2. Abrir navegador
http://localhost:8080/os/5305/visualizar

# 3. Verificar layout e dados renderizados
```

---

## 📚 Referências

- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Flying Saucer PDF](https://github.com/flyingsaucerproject/flyingsaucer)
- [iText PDF (alternativa)](https://itextpdf.com/)

---

## ✅ Checklist de Validação

- [x] Template HTML com sintaxe Thymeleaf correta
- [x] CSS compatível com geradores de PDF
- [x] Proteção contra valores nulos
- [x] Layout responsivo usando tabelas
- [x] DTO criado com todos os campos necessários
- [x] Controller de exemplo funcional
- [x] Documentação de uso completa
- [x] Template copiado para `src/main/resources/templates/`

**Status**: ✅ **Template validado e pronto para uso!**

