package br.com.deskinstaller.repository;

import br.com.deskinstaller.model.Apcliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações de acesso a dados da entidade Apcliente
 *
 * Fornece métodos básicos de persistência via JpaRepository e alguns
 * métodos de busca derivada úteis para o domínio.
 *
 * @author Julio Izidoro
 * @since 2025-11-13
 */
@Repository
public interface ApClienteRepository extends JpaRepository<Apcliente, Integer> {

    // Busca ApCliente por id do cliente (coluna cliente / cliente_idcliente)
    List<Apcliente> findByCliente(Integer idCliente);

}
