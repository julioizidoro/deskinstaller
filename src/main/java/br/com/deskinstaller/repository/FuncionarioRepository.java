package br.com.deskinstaller.repository;

import br.com.deskinstaller.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações de acesso a dados da entidade Funcionários
 *
 * Fornece métodos básicos de persistência via JpaRepository e alguns
 * métodos de busca derivada úteis para o domínio.
 *
 * @author Julio Izidoro
 * @since 2025-11-13
 */
@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {

    // Busca funcionários pelo status ativo
    List<Funcionario> findByAtivo(boolean ativo);

}
