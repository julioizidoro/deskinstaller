package br.com.deskinstaller.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.deskinstaller.model.Fichaatendimentoservico;

/**
 * Repository dos itens de servico da ficha de atendimento.
 *
 * @author Julio Izidoro
 */
@Repository
public interface FichaAtendimentoServicoRepository extends JpaRepository<Fichaatendimentoservico, Integer> {

    List<Fichaatendimentoservico> findByFichaatendimentoIdfichaatendimento(Integer idfichaatendimento);

    void deleteByFichaatendimentoIdfichaatendimento(Integer idfichaatendimento);
}
