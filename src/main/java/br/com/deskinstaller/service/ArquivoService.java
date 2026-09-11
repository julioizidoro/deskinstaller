package br.com.deskinstaller.service;

import br.com.deskinstaller.config.AwsS3Properties;
import br.com.deskinstaller.config.FileStorageProperties;
import br.com.deskinstaller.dto.ArquivoDTO;
import br.com.deskinstaller.exception.BusinessException;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.Arquivo;
import br.com.deskinstaller.repository.ArquivoRepository;
import br.com.deskinstaller.service.storage.FileStorage;
import br.com.deskinstaller.service.storage.LocalFileStorage;
import br.com.deskinstaller.service.storage.S3FileStorage;
import br.com.deskinstaller.service.storage.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Upload, consulta e remocao de arquivos.
 *
 * <p>A pasta e escolhida por quem chama (ex.: {@code cliente/1956/os/5455}),
 * mas passa por sanitizacao antes de virar chave: sem {@code ..}, sem barra
 * inicial, sem caractere fora do conjunto permitido. A chave final sempre
 * comeca pelo prefixo fixo da aplicacao ({@code deskinstaller/dev}), de modo
 * que nenhum caminho informado consegue escapar dele.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ArquivoService {

    /** Um ou mais segmentos separados por barra, sem ".." e sem espaco. */
    private static final Pattern PASTA_VALIDA =
            Pattern.compile("^[A-Za-z0-9._-]+(/[A-Za-z0-9._-]+)*$");

    private static final int TAMANHO_MAXIMO_PASTA = 400;

    private final ArquivoRepository arquivoRepository;
    private final S3FileStorage s3FileStorage;
    private final LocalFileStorage localFileStorage;
    private final FileStorageProperties fileStorageProperties;
    private final AwsS3Properties awsS3Properties;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    // ==========================================================
    // Upload
    // ==========================================================
    @Transactional
    public ArquivoDTO upload(MultipartFile arquivo, String pasta, String refTipo,
                            Integer refId, String descricao) {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessException("Nenhum arquivo foi enviado.");
        }
        if (arquivo.getSize() > fileStorageProperties.getMaxFileSizeBytes()) {
            throw new BusinessException("Arquivo acima do limite de "
                    + (fileStorageProperties.getMaxFileSizeBytes() / (1024 * 1024)) + " MB.");
        }

        String pastaLimpa = sanitizarPasta(pasta);
        String nomeOriginal = nomeOriginalSeguro(arquivo.getOriginalFilename());
        String nomeArmazenado = UUID.randomUUID() + extensao(nomeOriginal);
        String chave = montarChave(pastaLimpa, nomeArmazenado);

        byte[] conteudo;
        try {
            conteudo = arquivo.getBytes();
        } catch (IOException ex) {
            throw new BusinessException("Não foi possível ler o arquivo enviado.");
        }

        FileStorage destino = escolherStorage();
        try {
            destino.gravar(chave, conteudo, arquivo.getContentType());
        } catch (StorageException ex) {
            // Rede de seguranca: se o S3 falhar e o fallback estiver ligado,
            // grava no disco e registra como LOCAL para reenvio posterior.
            if (destino == s3FileStorage && fileStorageProperties.isLocalFallback()) {
                log.error("Falha ao gravar no S3; caindo para o disco local.", ex);
                destino = localFileStorage;
                destino.gravar(chave, conteudo, arquivo.getContentType());
            } else {
                throw ex;
            }
        }

        Arquivo entidade = new Arquivo();
        entidade.setProvider(destino.tipo());
        entidade.setBucket("S3".equals(destino.tipo()) ? awsS3Properties.getS3().getBucket() : null);
        entidade.setChave(chave);
        entidade.setPasta(pastaLimpa);
        entidade.setNomeOriginal(nomeOriginal);
        entidade.setNomeArmazenado(nomeArmazenado);
        entidade.setContentType(arquivo.getContentType());
        entidade.setTamanho(arquivo.getSize());
        entidade.setRefTipo(normalizarRefTipo(refTipo));
        entidade.setRefId(refId);
        entidade.setDescricao(descricao);
        entidade.setDataUpload(LocalDateTime.now());
        usuarioAutenticadoService.idUsuarioAtual().ifPresent(entidade::setUsuarioidusuario);

        Arquivo salvo = arquivoRepository.save(entidade);
        log.info("Arquivo {} registrado ({} bytes) em {}", salvo.getIdarquivo(), salvo.getTamanho(), chave);
        return converter(salvo, true);
    }

    // ==========================================================
    // Consulta
    // ==========================================================
    @Transactional(readOnly = true)
    public List<ArquivoDTO> listarPorReferencia(String refTipo, Integer refId) {
        return arquivoRepository
                .findByRefTipoAndRefIdOrderByDataUploadDesc(normalizarRefTipo(refTipo), refId)
                .stream()
                .map(a -> converter(a, true))
                .collect(Collectors.toList());
    }

    /**
     * Lista o conteudo de uma pasta. Com {@code incluirSubpastas}, traz tudo que
     * esta abaixo dela — util para "todos os arquivos do cliente 1956".
     */
    @Transactional(readOnly = true)
    public List<ArquivoDTO> listarPorPasta(String pasta, boolean incluirSubpastas) {
        String pastaLimpa = sanitizarPasta(pasta);
        List<Arquivo> encontrados = incluirSubpastas
                ? arquivoRepository.findByPastaStartingWithOrderByDataUploadDesc(pastaLimpa)
                : arquivoRepository.findByPastaOrderByDataUploadDesc(pastaLimpa);
        return encontrados.stream().map(a -> converter(a, true)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ArquivoDTO buscarPorId(Integer id) {
        return converter(exigir(id), true);
    }

    /** Conteudo do arquivo, para o endpoint de download da propria API. */
    @Transactional(readOnly = true)
    public ConteudoArquivo baixar(Integer id) {
        Arquivo arquivo = exigir(id);
        byte[] conteudo = storageDe(arquivo).ler(arquivo.getChave());
        return new ConteudoArquivo(arquivo.getNomeOriginal(), arquivo.getContentType(), conteudo);
    }

    // ==========================================================
    // Remocao
    // ==========================================================
    /**
     * Remove o binario e o registro. Se o binario ja nao existir no storage, o
     * registro e removido mesmo assim — nao faz sentido manter metadado orfao.
     */
    @Transactional
    public void deletar(Integer id) {
        Arquivo arquivo = exigir(id);
        try {
            storageDe(arquivo).remover(arquivo.getChave());
        } catch (StorageException ex) {
            log.error("Falha ao remover o binario {} do storage; o registro sera apagado assim mesmo.",
                    arquivo.getChave(), ex);
        }
        arquivoRepository.delete(arquivo);
        log.info("Arquivo {} removido.", id);
    }

    // ==========================================================
    // Apoio
    // ==========================================================
    private Arquivo exigir(Integer id) {
        return arquivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Arquivo não encontrado com ID: " + id));
    }

    private FileStorage escolherStorage() {
        if (fileStorageProperties.isS3Preferido()) {
            if (s3FileStorage.disponivel()) {
                return s3FileStorage;
            }
            if (!fileStorageProperties.isLocalFallback()) {
                throw new BusinessException("Armazenamento S3 não configurado.");
            }
            log.warn("S3 selecionado mas sem credenciais; gravando no disco local.");
        }
        return localFileStorage;
    }

    private FileStorage storageDe(Arquivo arquivo) {
        return "S3".equals(arquivo.getProvider()) ? s3FileStorage : localFileStorage;
    }

    /**
     * Aceita "cliente/1956/os/5455", "/cliente/1956/os/5455/" e recusa qualquer
     * coisa com "..", barra dupla ou caractere fora de [A-Za-z0-9._-].
     */
    String sanitizarPasta(String pasta) {
        if (pasta == null || pasta.isBlank()) {
            throw new BusinessException("A pasta de destino é obrigatória.");
        }
        String limpa = pasta.trim().replace('\\', '/');
        while (limpa.startsWith("/")) {
            limpa = limpa.substring(1);
        }
        while (limpa.endsWith("/")) {
            limpa = limpa.substring(0, limpa.length() - 1);
        }
        if (limpa.isEmpty() || limpa.contains("//")) {
            throw new BusinessException("Pasta de destino inválida: " + pasta);
        }
        if (limpa.length() > TAMANHO_MAXIMO_PASTA) {
            throw new BusinessException("Pasta de destino muito longa (máximo "
                    + TAMANHO_MAXIMO_PASTA + " caracteres).");
        }
        if (!PASTA_VALIDA.matcher(limpa).matches()) {
            throw new BusinessException("Pasta de destino inválida: use apenas letras, "
                    + "números, ponto, hífen, sublinhado e barra. Recebido: " + pasta);
        }
        for (String segmento : limpa.split("/")) {
            if (".".equals(segmento) || "..".equals(segmento)) {
                throw new BusinessException("Pasta de destino inválida: " + pasta);
            }
        }
        return limpa;
    }

    private String montarChave(String pasta, String nomeArmazenado) {
        String prefixo = awsS3Properties.getS3().getPrefix();
        prefixo = prefixo == null ? "" : prefixo.trim();
        while (prefixo.startsWith("/")) {
            prefixo = prefixo.substring(1);
        }
        while (prefixo.endsWith("/")) {
            prefixo = prefixo.substring(0, prefixo.length() - 1);
        }
        return prefixo.isEmpty()
                ? pasta + "/" + nomeArmazenado
                : prefixo + "/" + pasta + "/" + nomeArmazenado;
    }

    /** Descarta caminho embutido no nome enviado pelo navegador e caracteres de risco. */
    private String nomeOriginalSeguro(String nome) {
        if (nome == null || nome.isBlank()) {
            return "arquivo";
        }
        String apenasNome = nome.replace('\\', '/');
        int barra = apenasNome.lastIndexOf('/');
        if (barra >= 0) {
            apenasNome = apenasNome.substring(barra + 1);
        }
        apenasNome = apenasNome.replaceAll("[\\p{Cntrl}\"]", "").trim();
        if (apenasNome.isEmpty() || ".".equals(apenasNome) || "..".equals(apenasNome)) {
            return "arquivo";
        }
        return apenasNome.length() > 255 ? apenasNome.substring(apenasNome.length() - 255) : apenasNome;
    }

    private String extensao(String nomeOriginal) {
        int ponto = nomeOriginal.lastIndexOf('.');
        if (ponto < 0 || ponto == nomeOriginal.length() - 1) {
            return "";
        }
        String ext = nomeOriginal.substring(ponto + 1).toLowerCase(Locale.ROOT);
        return ext.matches("[a-z0-9]{1,10}") ? "." + ext : "";
    }

    private String normalizarRefTipo(String refTipo) {
        return refTipo == null || refTipo.isBlank() ? null : refTipo.trim().toUpperCase(Locale.ROOT);
    }

    private ArquivoDTO converter(Arquivo arquivo, boolean comUrl) {
        Optional<String> url = comUrl && "S3".equals(arquivo.getProvider())
                ? s3FileStorage.urlAssinada(arquivo.getChave(), arquivo.getNomeOriginal())
                : Optional.empty();

        return ArquivoDTO.builder()
                .idarquivo(arquivo.getIdarquivo())
                .pasta(arquivo.getPasta())
                .nomeOriginal(arquivo.getNomeOriginal())
                .contentType(arquivo.getContentType())
                .tamanho(arquivo.getTamanho())
                .refTipo(arquivo.getRefTipo())
                .refId(arquivo.getRefId())
                .descricao(arquivo.getDescricao())
                .dataUpload(arquivo.getDataUpload())
                .provider(arquivo.getProvider())
                .usuarioidusuario(arquivo.getUsuarioidusuario())
                .urlDownload(url.orElse(null))
                .build();
    }

    /** Binario devolvido pelo endpoint de download. */
    public record ConteudoArquivo(String nomeOriginal, String contentType, byte[] conteudo) {
    }
}
