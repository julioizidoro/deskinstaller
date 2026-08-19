package br.com.deskinstaller.repository;

import br.com.deskinstaller.model.Relservico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

/**
 * Repository para operações de acesso a dados da entidade RelServico
 *
 * Fornece métodos básicos de persistência via JpaRepository e alguns
 * métodos de busca derivada úteis para o domínio.
 *
 * @author Julio Izidoro
 * @since 2025-12-04
 */
@Repository
public interface RelServicoRepository extends JpaRepository<Relservico, Integer> {

    /**
     * Busca todos os itens de serviço relacionados a uma ordem de serviço.
     *
     * Observação: o nome da propriedade na entidade `Relservico` é `ordemservico` (lowercase),
     * por isso o método segue a convenção de nomes do Spring Data: `findBy` + `Ordemservico`.
     *
     * @param ordemservico id da ordem de serviço
     * @return lista de Relservico associados
     */
    List<Relservico> findByOrdemservico(int ordemservico);
}
