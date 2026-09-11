package br.com.deskinstaller.repository;

import br.com.deskinstaller.model.Arquivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArquivoRepository extends JpaRepository<Arquivo, Integer> {

    List<Arquivo> findByRefTipoAndRefIdOrderByDataUploadDesc(String refTipo, Integer refId);

    List<Arquivo> findByPastaOrderByDataUploadDesc(String pasta);

    /** Traz tudo que esta sob uma pasta e suas subpastas (pasta LIKE 'cliente/1956%'). */
    List<Arquivo> findByPastaStartingWithOrderByDataUploadDesc(String prefixoDaPasta);

    Optional<Arquivo> findByChave(String chave);
}
