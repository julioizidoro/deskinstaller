package br.com.deskinstaller.repository;

import br.com.deskinstaller.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de acesso a dados da entidade Cliente
 *
 * @author Julio Izidoro
 * @since 2025-11-13
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    /**
     * Busca clientes por nome (case insensitive, busca parcial)
     */
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca cliente por email exato
     */
    Optional<Cliente> findByEmail(String email);

    /**
     * Busca clientes por telefone celular
     */
    List<Cliente> findByFoneCelular(String foneCelular);

    /**
     * Busca clientes por fone residencial
     */
    List<Cliente> findByFoneResidencial(String foneResidencial);

    /**
     * Busca clientes nascidos entre duas datas
     */
    List<Cliente> findByDataNascimentoBetween(Date dataInicio, Date dataFim);

    /**
     * Busca clientes por contato
     */
    List<Cliente> findByContatoContainingIgnoreCase(String contato);

    /**
     * Query customizada para buscar clientes com múltiplos critérios
     */
    @Query("SELECT c FROM Cliente c WHERE " +
           "(:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:email IS NULL OR c.email = :email) AND " +
           "(:telefone IS NULL OR c.foneCelular = :telefone OR c.foneResidencial = :telefone)")
    List<Cliente> buscarComFiltros(
        @Param("nome") String nome,
        @Param("email") String email,
        @Param("telefone") String telefone
    );

    /**
     * Verifica se existe cliente com o email informado
     */
    boolean existsByEmail(String email);

    /**
     * Conta quantos clientes existem
     */
    @Query("SELECT COUNT(c) FROM Cliente c")
    long contarClientes();
}

