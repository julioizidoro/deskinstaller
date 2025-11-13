#!/usr/bin/env python3
import os
import re

MODEL_DIR = "/Users/julioizidoro/Git/avaliacao-outsera/src/main/java/br/com/deskinstaller/model"

def clean_and_add_lombok(filepath):
    """Limpa o arquivo e adiciona Lombok"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Pula se já tem @Data
    if '@Data' in content:
        return False

    # Adiciona imports Lombok
    if 'import lombok' not in content:
        # Encontra último import jakarta.persistence
        last_import_pos = 0
        for match in re.finditer(r'import jakarta\.persistence\.[^;]+;', content):
            last_import_pos = match.end()

        if last_import_pos > 0:
            lombok_imports = '\nimport lombok.AllArgsConstructor;\nimport lombok.Data;\nimport lombok.NoArgsConstructor;\n'
            content = content[:last_import_pos] + lombok_imports + content[last_import_pos:]

    # Adiciona anotações antes de @Entity
    if '@Entity' in content and '@Data' not in content:
        content = re.sub(
            r'(@Entity)',
            r'@Data\n@NoArgsConstructor\n@AllArgsConstructor\n\1',
            content,
            count=1
        )

    # Remove construtores customizados simples
    content = re.sub(r'\n\s+public \w+\([^)]*\)\s*\{\s*this\.\w+\s*=\s*\w+;\s*\}', '', content)

    # Remove código quebrado (chaves extras, etc)
    content = re.sub(r'\}\s*\}\s*return true;\s*\}', '', content)
    content = re.sub(r'\s+\w+ other = \(\w+\) object;.*?return true;\s*\}', '', content, flags=re.DOTALL)

    # Limpa linhas em branco múltiplas
    content = re.sub(r'\n\s*\n\s*\n+', '\n\n', content)

    # Garante que termina com }
    content = content.rstrip() + '\n'
    if not content.strip().endswith('}'):
        content += '}\n'

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

    return True

# Processar todos os arquivos
for filename in sorted(os.listdir(MODEL_DIR)):
    if filename.endswith('.java') and not filename.endswith('.bak'):
        filepath = os.path.join(MODEL_DIR, filename)
        if clean_and_lombok(filepath):
            print(f"✅ {filename}")
        else:
            print(f"⏭️  {filename} (já tem Lombok)")

print("\n✅ Processamento concluído!")

