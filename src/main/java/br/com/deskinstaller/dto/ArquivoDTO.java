package br.com.deskinstaller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Metadados de um arquivo devolvidos ao front. Nunca carrega o binario. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArquivoDTO {

    private Integer idarquivo;
    private String pasta;
    private String nomeOriginal;
    private String contentType;
    private Long tamanho;
    private String refTipo;
    private Integer refId;
    private String descricao;
    private LocalDateTime dataUpload;
    private String provider;
    private Integer usuarioidusuario;

    /** URL temporaria de download (S3). Nula no provider local — use /download. */
    private String urlDownload;
}
