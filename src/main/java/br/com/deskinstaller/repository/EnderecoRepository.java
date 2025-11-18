package br.com.deskinstaller.repository;

import br.com.deskinstaller.model.Endereco;
import br.com.deskinstaller.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações de acesso a dados da entidade Funcionario
 *
 * Fornece métodos básicos de persistência via JpaRepository e alguns
 * métodos de busca derivada úteis para o domínio.
 *
 * @author Julio Izidoro
 * @since 2025-11-13
 */
@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {

    /**
     * Busca endereços pelo id do cliente (coluna cliente / cliente_idcliente)
     */
    List<Endereco> findByCliente(Integer idCliente);
}
