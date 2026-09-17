package br.com.deskinstaller.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.deskinstaller.model.Orcamento;

/**
 * Repository da entidade Orcamento.
 *
 * @author Julio Izidoro
 */
@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Integer> {

    List<Orcamento> findByClienteOrderByDataemissaoDesc(int cliente);

    List<Orcamento> findByEnderecoOrderByDataemissaoDesc(Integer endereco);

    List<Orcamento> findBySituacaoOrderByDataemissaoDesc(String situacao);

    List<Orcamento> findByDataemissaoBetweenOrderByDataemissaoDesc(Date inicio, Date fim);
}
