package br.com.deskinstaller.repository;

import br.com.deskinstaller.model.Contasreceber;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository para operações de acesso a dados da entidade Contasreceber.
 *
 * @author Julio Izidoro
 */
@Repository
public interface ContasReceberRepository extends JpaRepository<Contasreceber, Integer> {

    /** Títulos lançados por um usuário. */
    List<Contasreceber> findByUsuarioidusuario(Integer usuarioidusuario);

    /** Títulos de um cliente, do vencimento mais antigo para o mais novo. */
    List<Contasreceber> findByCliente_IdclienteOrderByDatavencimentoAsc(Integer clienteId);

    /** Títulos com vencimento dentro do intervalo (inclusive). */
    List<Contasreceber> findByDatavencimentoBetweenOrderByDatavencimentoAsc(LocalDate inicio, LocalDate fim);

    /** Títulos ainda em aberto na data informada. */
    List<Contasreceber> findByDatavencimentoLessThanEqualOrderByDatavencimentoAsc(LocalDate limite);

    /** Busca pelo número da nota fiscal. */
    List<Contasreceber> findByNumeronf(String numeronf);

    /**
     * Títulos em aberto: sem data de recebimento e sem valor recebido.
     * A coluna aceita NULL e tem default 0.00, por isso os dois casos contam
     * como "nada recebido".
     */
    @Query("select c from Contasreceber c "
            + "where c.datarecebimento is null "
            + "and (c.valorrecebido is null or c.valorrecebido = 0) "
            + "order by c.datavencimento asc")
    List<Contasreceber> findEmAberto();
}
