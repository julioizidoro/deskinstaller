package br.com.deskinstaller.repository;

import br.com.deskinstaller.model.Relservico;
import br.com.deskinstaller.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações de acesso a dados da entidade Servico
 *
 * Fornece métodos básicos de persistência via JpaRepository e alguns
 * métodos de busca derivada úteis para o domínio.
 *
 * @author Julio Izidoro
 * @since 2025-12-04
 */
@Repository
public interface ServicoRepository extends JpaRepository<Servico, Integer> {
    // Busca SErvicos com situacao ativa
    List<Servico> findBySituacao(boolean situacao);
}
