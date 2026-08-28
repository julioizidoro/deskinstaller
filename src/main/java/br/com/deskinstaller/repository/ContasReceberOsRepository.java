package br.com.deskinstaller.repository;

import br.com.deskinstaller.model.Contasreceberos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações de acesso a dados da entidade Contasreceberos.
 *
 * @author Julio Izidoro
 */
@Repository
public interface ContasReceberOsRepository extends JpaRepository<Contasreceberos, Integer> {

    /** Vínculos de um título a receber. */
    List<Contasreceberos> findByContasreceberidcontasreceber(Integer contasreceberidcontasreceber);

    /** Vínculos de uma ordem de serviço. */
    List<Contasreceberos> findByOrdemservicoidordemServico(Integer ordemservicoidordemServico);

    /** Evita vincular duas vezes o mesmo título à mesma OS. */
    boolean existsByContasreceberidcontasreceberAndOrdemservicoidordemServico(
            Integer contasreceberidcontasreceber, Integer ordemservicoidordemServico);

    void deleteByContasreceberidcontasreceber(Integer contasreceberidcontasreceber);
}
