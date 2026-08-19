package br.com.deskinstaller.repository;

import br.com.deskinstaller.model.Ordemservico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de acesso a dados da entidade Ordemservico
 *
 * @author Julio Izidoro
 * @since 2025-11-13
 */
@Repository
public interface OrdemServicoRepository extends JpaRepository<Ordemservico, Integer> {

    // Lista ordens com situação diferente de Cancelada e Finalizada e datasituacao nos últimos 7 dias
    List<Ordemservico> findBySituacaoNotInAndDatasituacaoGreaterThanEqual(List<String> situacoes, Date limite);

    // Busca ordens cuja situação NÃO está nas informadas OR cuja datasituacao é >= limite (últimos 7 dias)
    List<Ordemservico> findBySituacaoNotInOrDatasituacaoGreaterThanEqual(List<String> situacoes, Date limite);

    // Busca com JOIN FETCH para garantir que o cliente seja carregado junto (evita LazyInitializationException / cliente nulo ao converter)
    @Query("select distinct o from Ordemservico o left join fetch o.cliente where (o.situacao not in :situacoes or o.datasituacao >= :limite)")
    List<Ordemservico> findBySituacaoNotInOrDatasituacaoGreaterThanEqualFetchCliente(@Param("situacoes") List<String> situacoes, @Param("limite") Date limite);

    // BUSCA COM FETCH DE CLIENTE E ENDERECO
    @Query("select distinct o from Ordemservico o left join fetch o.cliente left join fetch o.endereco where (o.situacao not in :situacoes or o.datasituacao >= :limite)")
    List<Ordemservico> findBySituacaoNotInOrDatasituacaoGreaterThanEqualFetchClienteAndEndereco(@Param("situacoes") List<String> situacoes, @Param("limite") Date limite);

    // Garantir carregamento do cliente com join fetch para evitar problemas de lazy loading
    @Query("select o from Ordemservico o left join fetch o.cliente where o.idordemServico = :id")
    Optional<Ordemservico> findByIdWithCliente(@Param("id") Integer id);

    @Query("select o from Ordemservico o left join fetch o.cliente left join fetch o.endereco where o.idordemServico = :id")
    Optional<Ordemservico> findByIdWithClienteAndEndereco(@Param("id") Integer id);

    @Query("select distinct o from Ordemservico o left join fetch o.cliente")
    List<Ordemservico> findAllWithCliente();
}
