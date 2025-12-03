package br.com.deskinstaller.repository;

import br.com.deskinstaller.model.OsFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações de acesso a dados da entidade OsFuncionario.
 *
 * @author Julio Izidoro
 * @since 2025-12-02
 */
@Repository
public interface OsFuncionarioRepository extends JpaRepository<OsFuncionario, Integer> {

    // Lista funcionários da Ordem de Serviço por id da ordem (usa propriedade de navegação ordemServico.id)
    List<OsFuncionario> findByOrdemServico(Integer ordemServico);
}
