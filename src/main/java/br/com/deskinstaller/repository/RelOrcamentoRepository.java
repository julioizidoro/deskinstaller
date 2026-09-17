package br.com.deskinstaller.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.deskinstaller.model.Relorcamento;

/**
 * Repository dos itens de orcamento.
 *
 * @author Julio Izidoro
 */
@Repository
public interface RelOrcamentoRepository extends JpaRepository<Relorcamento, Integer> {

    List<Relorcamento> findByOrcamento(int orcamento);

    void deleteByOrcamento(int orcamento);
}
