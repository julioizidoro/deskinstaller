#!/bin/bash

# Script para limpar getters/setters/construtores das entidades com Lombok

MODEL_DIR="/Users/julioizidoro/Git/avaliacao-outsera/src/main/java/br/com/deskinstaller/model"

echo "🧹 Limpando código boilerplate das entidades com Lombok..."
echo ""

cd "$MODEL_DIR" || exit 1

for file in *.java; do
    if [ -f "$file" ]; then
        echo "  📄 Processando $file..."

        # Backup
        cp "$file" "$file.bak"

        # Remove getters (public Type getName() { return name; })
        perl -i -p0e 's/\s+public\s+\w+(\[\])?\s+get\w+\([^\)]*\)\s*\{[^\}]+\}//gs' "$file"

        # Remove setters (public void setName(Type name) { this.name = name; })
        perl -i -p0e 's/\s+public\s+void\s+set\w+\([^\)]*\)\s*\{[^\}]+\}//gs' "$file"

        # Remove construtores vazios
        perl -i -p0e 's/\s+public\s+\w+\(\)\s*\{\s*\}//gs' "$file"

        # Remove hashCode
        perl -i -p0e 's/\s+@Override\s+public\s+int\s+hashCode\(\)\s*\{[^\}]+\}//gs' "$file"

        # Remove equals parcial/quebrado
        perl -i -p0e 's/\s+\w+\s+other\s*=\s*\(\w+\)\s*object;[^\}]*//gs' "$file"

        # Remove toString
        perl -i -p0e 's/\s+@Override\s+public\s+String\s+toString\(\)\s*\{[^\}]+\}//gs' "$file"

        # Limpa linhas vazias múltiplas
        perl -i -p0e 's/\n\s*\n\s*\n+/\n\n/gs' "$file"

        # Remove linhas vazias antes do fechamento da classe
        perl -i -pe 's/^\s*\n}$/}/' "$file"

        echo "    ✅ Limpo"
    fi
done

echo ""
echo "✅ Concluído! Backups salvos com extensão .bak"
echo "   Para remover os backups: rm $MODEL_DIR/*.bak"

