package br.com.deskinstaller.repository;

import br.com.deskinstaller.model.Obstecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações de acesso a dados da entidade ObsTecnico
 *
 * Fornece métodos básicos de persistência via JpaRepository e alguns
 * métodos de busca derivada úteis para o domínio.
 *
 * @author Julio Izidoro
 * @since 2025-12-09
 */
@Repository
public interface ObsTecnicoRepository extends JpaRepository<Obstecnico, Integer> {
    /**
     * Busca todos os itens de obsTecnicos relacionados a uma ordem de serviço.
     *
     * Observação: a propriedade na entidade `Obstecnico` é `ordemServico` (camelCase),
     * então o método derivado deve seguir essa convenção.
     *
     * @param ordemServico id da ordem de serviço
     * @return lista de Obstecnico associados
     */
    List<Obstecnico> findByOrdemServico(Integer ordemServico);
}
