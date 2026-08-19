package br.com.deskinstaller.repository;

import br.com.deskinstaller.model.OsFinanceiro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações de acesso a dados da entidade OsFinanceiro
 *
 * Fornece métodos básicos de persistência via JpaRepository e alguns
 * métodos de busca derivada úteis para o domínio.
 *
 * @author Julio Izidoro
 * @since 2025-12-10
 */
@Repository
public interface OsFinanceiroRepository extends JpaRepository<OsFinanceiro, Integer> {

    /**
     * Busca todos os itens de serviço relacionados a uma ordem de serviço.
     *
     * Observação: o nome da propriedade na entidade `OsFinanceiro` é `ordemservico` (lowercase),
     * por isso o método segue a convenção de nomes do Spring Data: `findBy` + `Ordemservico`.
     *
     * @param ordemservico id da ordem de serviço
     * @return lista de OsFinanceiro associados
     */
    List<OsFinanceiro> findByOrdemservico(int ordemservico);
}
