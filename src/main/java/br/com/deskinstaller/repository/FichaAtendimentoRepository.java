package br.com.deskinstaller.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.deskinstaller.model.Fichaatendimento;

/**
 * Repository da entidade Fichaatendimento.
 *
 * @author Julio Izidoro
 */
@Repository
public interface FichaAtendimentoRepository extends JpaRepository<Fichaatendimento, Integer> {

    List<Fichaatendimento> findByClienteIdclienteOrderByDatavisitaDesc(Integer idcliente);

    List<Fichaatendimento> findByEnderecoIdenderecoOrderByDatavisitaDesc(Integer idendereco);

    List<Fichaatendimento> findByFuncionarioIdfuncionarioOrderByDatavisitaDesc(Integer idfuncionario);

    List<Fichaatendimento> findByDatavisitaBetweenOrderByDatavisitaDesc(LocalDate inicio, LocalDate fim);
}
