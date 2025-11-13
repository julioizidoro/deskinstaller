#!/usr/bin/env python3
"""
Script para adicionar anotações Lombok nas entidades JPA
"""

import os
import re
from pathlib import Path

MODEL_DIR = "/Users/julioizidoro/Git/avaliacao-outsera/src/main/java/br/com/deskinstaller/model"

def add_lombok_to_entity(file_path):
    """Adiciona anotações Lombok a uma entidade JPA"""

    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Verifica se já tem Lombok
    if '@Data' in content or 'import lombok' in content:
        print(f"  ⏭️  {os.path.basename(file_path)} - Já tem Lombok")
        return False

    # Adiciona imports do Lombok após os imports do Jakarta
    lombok_imports = """import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

"""

    # Procura pela última linha de import jakarta.persistence
    import_pattern = r'(import jakarta\.persistence\.[^;]+;)\s*\n'
    matches = list(re.finditer(import_pattern, content))

    if matches:
        last_import = matches[-1]
        insert_pos = last_import.end()
        content = content[:insert_pos] + "\n" + lombok_imports + content[insert_pos:]
    else:
        # Se não encontrou imports jakarta, procura por imports java
        import_pattern = r'(import java\.[^;]+;)\s*\n'
        matches = list(re.finditer(import_pattern, content))
        if matches:
            last_import = matches[-1]
            insert_pos = last_import.end()
            content = content[:insert_pos] + "\n" + lombok_imports + content[insert_pos:]

    # Adiciona @Data, @NoArgsConstructor, @AllArgsConstructor antes de @Entity
    entity_pattern = r'(@Entity\s*\n)'
    replacement = r'@Data\n@NoArgsConstructor\n@AllArgsConstructor\n\1'
    content = re.sub(entity_pattern, replacement, content)

    # Remove getters e setters (padrão JavaBeans)
    # Remove métodos get/set e seus blocos
    content = re.sub(r'\s+public \w+(\[\])? get\w+\([^)]*\)\s*\{[^}]+\}', '', content)
    content = re.sub(r'\s+public void set\w+\([^)]*\)\s*\{[^}]+\}', '', content)

    # Remove construtores vazios
    class_name = os.path.basename(file_path).replace('.java', '')
    content = re.sub(rf'\s+public {class_name}\(\)\s*\{{\s*\}}', '', content)

    # Remove métodos hashCode, equals, toString
    content = re.sub(r'\s+@Override\s+public int hashCode\(\)\s*\{[^}]+\}', '', content)
    content = re.sub(r'\s+@Override\s+public boolean equals\(Object [^)]+\)\s*\{[^}]+\}', '', content)
    content = re.sub(r'\s+@Override\s+public String toString\(\)\s*\{[^}]+\}', '', content)

    # Limpa linhas vazias múltiplas
    content = re.sub(r'\n\s*\n\s*\n+', '\n\n', content)

    # Salva o arquivo modificado
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

    print(f"  ✅ {os.path.basename(file_path)} - Lombok adicionado")
    return True

def main():
    print("🚀 Aplicando Lombok nas entidades JPA...\n")

    model_path = Path(MODEL_DIR)
    java_files = sorted(model_path.glob("*.java"))

    if not java_files:
        print(f"❌ Nenhum arquivo .java encontrado em {MODEL_DIR}")
        return

    total = len(java_files)
    modified = 0

    for java_file in java_files:
        if add_lombok_to_entity(str(java_file)):
            modified += 1

    print(f"\n✅ Concluído!")
    print(f"   Total de arquivos: {total}")
    print(f"   Modificados: {modified}")
    print(f"   Já tinham Lombok: {total - modified}")

if __name__ == "__main__":
    main()

