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

    // Busca RelServico por idOrdemServico (nome de propriedade correto: ordemservico)
    List<Relservico> findByOrdemservico(Integer idOrdemServico);
}
