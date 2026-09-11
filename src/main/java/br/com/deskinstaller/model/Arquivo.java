package br.com.deskinstaller.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Metadados de um arquivo enviado pela aplicacao.
 *
 * <p>O binario vive no storage (S3 ou disco local); aqui fica apenas o que
 * permite localiza-lo depois: a chave completa, a pasta logica e a referencia
 * de negocio (ex.: OS 5455, aparelho 33).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "arquivo")
public class Arquivo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idarquivo")
    private Integer idarquivo;

    /** Onde o binario esta: S3 ou LOCAL. */
    @Column(name = "provider", nullable = false, length = 10)
    private String provider;

    /** Bucket usado quando provider = S3. */
    @Column(name = "bucket", length = 120)
    private String bucket;

    /** Chave completa dentro do bucket (ja inclui o prefixo fixo). */
    @Column(name = "chave", nullable = false, length = 500, unique = true)
    private String chave;

    /** Pasta logica informada no upload, ex.: cliente/1956/os/5455 */
    @Column(name = "pasta", nullable = false, length = 400)
    private String pasta;

    /** Nome do arquivo como o usuario enviou. */
    @Column(name = "nome_original", nullable = false, length = 255)
    private String nomeOriginal;

    /** Nome gerado para gravacao (uuid + extensao), evita colisao e nome perigoso. */
    @Column(name = "nome_armazenado", nullable = false, length = 255)
    private String nomeArmazenado;

    @Column(name = "content_type", length = 150)
    private String contentType;

    @Column(name = "tamanho")
    private Long tamanho;

    /** Tipo da entidade dona do arquivo: OS, APARELHO, CLIENTE... */
    @Column(name = "ref_tipo", length = 40)
    private String refTipo;

    /** Id da entidade dona, no tipo INT usado pelas chaves do banco legado. */
    @Column(name = "ref_id")
    private Integer refId;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "data_upload", nullable = false)
    private LocalDateTime dataUpload;

    /** Quem enviou (usuario.idusuario). */
    @Column(name = "usuario_idusuario")
    private Integer usuarioidusuario;
}
