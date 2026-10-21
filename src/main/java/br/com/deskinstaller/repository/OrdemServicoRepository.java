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

    /**
     * Pesquisa por numero da OS, nome do cliente e/ou endereco, em qualquer situacao,
     * limitada as OS com dataServico >= limite. Filtro vazio ("" ou null) e ignorado.
     */
    @Query("select distinct o from Ordemservico o left join fetch o.cliente c left join fetch o.endereco e "
         + "where (o.dataServico is null or o.dataServico >= :limite) "
         + "and (:numero is null or o.idordemServico = :numero) "
         + "and (:cliente = '' or lower(c.nome) like lower(concat('%', :cliente, '%'))) "
         + "and (:endereco = '' or lower(concat(coalesce(e.tipoLogradouro, ''), ' ', coalesce(e.logradouro, ''), ' ', "
         + "coalesce(e.numero, ''), ' ', coalesce(e.complemento, ''), ' ', coalesce(e.bairro, ''), ' ', "
         + "coalesce(e.cidade, ''))) like lower(concat('%', :endereco, '%'))) "
         + "order by o.dataServico desc")
    List<Ordemservico> pesquisar(@Param("numero") Integer numero,
                                 @Param("cliente") String cliente,
                                 @Param("endereco") String endereco,
                                 @Param("limite") Date limite);

    /** Abertas (sempre) + Finalizadas/Canceladas cuja datasituacao e >= limite (ultimos 7 dias). */
    @Query("select distinct o from Ordemservico o left join fetch o.cliente left join fetch o.endereco "
         + "where o.situacao = 'Aberta' "
         + "or (o.situacao in ('Finalizada', 'Cancelada') and o.datasituacao >= :limite)")
    List<Ordemservico> findAbertasOuEncerradasDesdeFetchClienteAndEndereco(@Param("limite") Date limite);

    // Garantir carregamento do cliente com join fetch para evitar problemas de lazy loading
    @Query("select o from Ordemservico o left join fetch o.cliente where o.idordemServico = :id")
    Optional<Ordemservico> findByIdWithCliente(@Param("id") Integer id);

    @Query("select o from Ordemservico o left join fetch o.cliente left join fetch o.endereco where o.idordemServico = :id")
    Optional<Ordemservico> findByIdWithClienteAndEndereco(@Param("id") Integer id);

    @Query("select distinct o from Ordemservico o left join fetch o.cliente")
    List<Ordemservico> findAllWithCliente();

    // Agenda do dia: ordens com dataServico dentro do intervalo informado (qualquer situação)
    @Query("select distinct o from Ordemservico o "
            + "left join fetch o.cliente left join fetch o.endereco "
            + "where o.dataServico between :inicio and :fim "
            + "order by o.horaServico asc")
    List<Ordemservico> findByDataServicoBetweenFetchClienteAndEndereco(
            @Param("inicio") Date inicio,
            @Param("fim") Date fim);

    // Agenda do dia de um técnico específico (funcionário alocado na OS)
    @Query("select distinct o from Ordemservico o "
            + "left join fetch o.cliente left join fetch o.endereco "
            + "join OsFuncionario f on f.ordemServico = o.idordemServico "
            + "where o.dataServico between :inicio and :fim "
            + "and f.funcionario.idfuncionario = :funcionarioId "
            + "order by o.horaServico asc")
    List<Ordemservico> findByDataServicoBetweenAndFuncionarioFetchClienteAndEndereco(
            @Param("inicio") Date inicio,
            @Param("fim") Date fim,
            @Param("funcionarioId") Integer funcionarioId);
}
