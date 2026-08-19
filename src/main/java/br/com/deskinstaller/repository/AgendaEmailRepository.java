package br.com.deskinstaller.repository;

import br.com.deskinstaller.model.AgendaEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository de AgendaEmail.
 *
 * <p>Nao expoe operacoes de remocao por decisao de negocio: enderecos saem de
 * circulacao pela flag {@code ativo}.
 */
@Repository
public interface AgendaEmailRepository extends JpaRepository<AgendaEmail, Integer> {

    List<AgendaEmail> findByAtivoOrderByEmailAsc(Boolean ativo);

    List<AgendaEmail> findAllByOrderByEmailAsc();

    Optional<AgendaEmail> findByEmailIgnoreCase(String email);

    List<AgendaEmail> findByEmailContainingIgnoreCaseOrderByEmailAsc(String trecho);
}
