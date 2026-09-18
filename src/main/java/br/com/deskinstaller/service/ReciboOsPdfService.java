package br.com.deskinstaller.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import br.com.deskinstaller.dto.ReciboParcelaDTO;
import br.com.deskinstaller.dto.ReciboServicoDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.model.Apcliente;
import br.com.deskinstaller.model.Cliente;
import br.com.deskinstaller.model.Contasreceber;
import br.com.deskinstaller.model.Contasreceberos;
import br.com.deskinstaller.model.Endereco;
import br.com.deskinstaller.model.Ordemservico;
import br.com.deskinstaller.model.OsFinanceiro;
import br.com.deskinstaller.model.Relservico;
import br.com.deskinstaller.repository.ContasReceberOsRepository;
import br.com.deskinstaller.repository.ContasReceberRepository;
import br.com.deskinstaller.repository.OrdemServicoRepository;
import br.com.deskinstaller.repository.OsFinanceiroRepository;
import br.com.deskinstaller.repository.RelServicoRepository;
import br.com.deskinstaller.util.ValorPorExtenso;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Monta o contexto e gera o recibo de recebimento dos valores de uma OS.
 * Mesmo esquema do orcamento: template Thymeleaf + OpenHTMLtoPDF.
 *
 * @author Julio Izidoro
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReciboOsPdfService {

    private static final String LOGO_CLASSPATH = "static/img/logo_onda_termica.png";
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_EXTENSO =
            DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));

    private final OrdemServicoRepository ordemServicoRepository;
    private final ContasReceberOsRepository contasReceberOsRepository;
    private final ContasReceberRepository contasReceberRepository;
    private final OsFinanceiroRepository osFinanceiroRepository;
    private final RelServicoRepository relServicoRepository;
    private final PdfGeneratorService pdfGeneratorService;

    /** Variaveis do template, reaproveitadas pelo PDF e pela previa em HTML. */
    @Transactional(readOnly = true)
    public Map<String, Object> montarModelo(Integer idOrdemServico) {
        Ordemservico os = ordemServicoRepository.findByIdWithClienteAndEndereco(idOrdemServico)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ordem de Serviço não encontrada: " + idOrdemServico));

        Cliente cliente = os.getCliente();
        Endereco endereco = os.getEndereco();
        List<OsFinanceiro> financeiro = osFinanceiroRepository.findByOrdemservico(idOrdemServico);
        List<Contasreceber> titulos = buscarTitulosDaOs(idOrdemServico);

        List<ReciboParcelaDTO> parcelas = montarParcelas(titulos, financeiro);
        BigDecimal total = parcelas.stream()
                .map(ReciboParcelaDTO::getValor)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.signum() == 0) {
            // Sem titulo recebido lancado: cai no valor da propria OS.
            total = BigDecimal.valueOf(os.getValor() - os.getValordesconto());
        }

        Map<String, Object> modelo = new LinkedHashMap<>();
        modelo.put("os", os);
        modelo.put("cliente", cliente);
        modelo.put("endereco", endereco);
        modelo.put("servicos", montarServicos(idOrdemServico));
        modelo.put("parcelas", parcelas);
        modelo.put("total", total);
        modelo.put("totalExtenso", ValorPorExtenso.converter(total));
        modelo.put("formaPagamento", formaPagamento(financeiro));
        modelo.put("dataServico", os.getDataServico() != null
                ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(os.getDataServico())
                : null);
        modelo.put("dataEmissao", LocalDate.now().format(DATA));
        modelo.put("dataExtenso", LocalDate.now().format(DATA_EXTENSO));
        modelo.put("cidade", endereco != null && endereco.getCidade() != null
                ? endereco.getCidade() : "Florianópolis");
        modelo.put("logoBase64", carregarLogoBase64());
        return modelo;
    }

    @Transactional(readOnly = true)
    public byte[] gerarPdf(Integer idOrdemServico) {
        log.info("Gerando recibo em PDF da OS {}", idOrdemServico);
        Context context = new Context();
        context.setVariables(montarModelo(idOrdemServico));
        return pdfGeneratorService.gerarPdfDeTemplate("ReciboOsHTML", context);
    }

    /**
     * Servicos executados na OS, com o equipamento atendido e o local onde ele esta
     * instalado — ex.: "Manutenção Preventiva" / "LG Split Hw Inverter 18.000 Btu/h" / "Sala".
     */
    private List<ReciboServicoDTO> montarServicos(Integer idOrdemServico) {
        List<Relservico> itens = relServicoRepository.findByOrdemservico(idOrdemServico);
        List<ReciboServicoDTO> servicos = new ArrayList<>();

        for (Relservico item : itens) {
            Apcliente aparelho = item.getApCliente();
            servicos.add(ReciboServicoDTO.builder()
                    .servico(descricaoServico(item))
                    .equipamento(descricaoEquipamento(aparelho))
                    .local(aparelho != null && aparelho.getLocal() != null && !aparelho.getLocal().isBlank()
                            ? aparelho.getLocal() : "-")
                    .quantidade(formatarQuantidade(item.getQuantidade()))
                    .build());
        }
        return servicos;
    }

    /** Nome do servico e, quando houver, a descricao complementar lancada no item. */
    private String descricaoServico(Relservico item) {
        StringBuilder sb = new StringBuilder();
        if (item.getServico() != null && item.getServico().getDescricao() != null) {
            sb.append(item.getServico().getDescricao());
        }
        if (item.getDescricao() != null && !item.getDescricao().isBlank()) {
            if (sb.length() > 0) {
                sb.append(": ");
            }
            sb.append(item.getDescricao().trim());
        }
        return sb.length() > 0 ? sb.toString() : "-";
    }

    /** Fabricante - modelo - capacidade, pulando o que estiver em branco. */
    private String descricaoEquipamento(Apcliente aparelho) {
        if (aparelho == null) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        acrescentar(sb, aparelho.getFabricante());
        acrescentar(sb, aparelho.getModelo());
        acrescentar(sb, aparelho.getCapacidade());
        return sb.length() > 0 ? sb.toString() : "-";
    }

    private void acrescentar(StringBuilder sb, String valor) {
        if (valor != null && !valor.isBlank()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(valor.trim());
        }
    }

    /** 1.0 vira "1"; 1.5 continua "1,5". */
    private String formatarQuantidade(double quantidade) {
        if (quantidade == Math.rint(quantidade)) {
            return String.valueOf((long) quantidade);
        }
        return String.format(new Locale("pt", "BR"), "%.2f", quantidade);
    }

    /** Titulos a receber vinculados a OS, na ordem de vencimento. */
    private List<Contasreceber> buscarTitulosDaOs(Integer idOrdemServico) {
        List<Integer> ids = contasReceberOsRepository.findByOrdemservicoidordemServico(idOrdemServico).stream()
                .map(Contasreceberos::getContasreceberidcontasreceber)
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        return contasReceberRepository.findAllById(ids);
    }

    /**
     * Monta as linhas do recibo. Considera recebido o titulo com data de
     * recebimento ou valor recebido maior que zero.
     */
    private List<ReciboParcelaDTO> montarParcelas(List<Contasreceber> titulos, List<OsFinanceiro> financeiro) {
        String forma = formaPagamento(financeiro);
        List<ReciboParcelaDTO> parcelas = new ArrayList<>();

        for (Contasreceber t : titulos) {
            boolean recebido = t.getDatarecebimento() != null
                    || (t.getValorrecebido() != null && t.getValorrecebido().signum() > 0);
            if (!recebido) {
                continue;
            }
            parcelas.add(ReciboParcelaDTO.builder()
                    .documento(descricaoDocumento(t))
                    .vencimento(t.getDatavencimento() != null ? t.getDatavencimento().format(DATA) : "-")
                    .recebimento(t.getDatarecebimento() != null ? t.getDatarecebimento().format(DATA) : "-")
                    .formaPagamento(forma)
                    .valor(t.getValorrecebido() != null ? t.getValorrecebido() : BigDecimal.ZERO)
                    .build());
        }

        parcelas.sort((a, b) -> a.getRecebimento().compareTo(b.getRecebimento()));

        // Nenhum titulo lancado: usa o que foi registrado no financeiro da OS.
        if (parcelas.isEmpty()) {
            for (OsFinanceiro f : financeiro) {
                if (f.getValorrecebido() == null || f.getValorrecebido() <= 0f) {
                    continue;
                }
                parcelas.add(ReciboParcelaDTO.builder()
                        .documento(f.getParcelas() > 1 ? f.getParcelas() + "x" : "À vista")
                        .vencimento("-")
                        .recebimento(f.getData() != null ? f.getData().format(DATA) : "-")
                        .formaPagamento(f.getFormapagamento() != null ? f.getFormapagamento() : forma)
                        .valor(BigDecimal.valueOf(f.getValorrecebido()))
                        .build());
            }
        }
        return parcelas;
    }

    private String descricaoDocumento(Contasreceber t) {
        if (t.getNumero() != null && !t.getNumero().isBlank()) {
            return t.getNumero();
        }
        if (t.getNumeroparcela() != null) {
            return "Parcela " + t.getNumeroparcela();
        }
        return "Título " + t.getIdcontasreceber();
    }

    private String formaPagamento(List<OsFinanceiro> financeiro) {
        return financeiro.stream()
                .map(OsFinanceiro::getFormapagamento)
                .filter(f -> f != null && !f.isBlank())
                .findFirst()
                .orElse("-");
    }

    private String carregarLogoBase64() {
        try (InputStream in = new ClassPathResource(LOGO_CLASSPATH).getInputStream()) {
            return Base64.getEncoder().encodeToString(in.readAllBytes());
        } catch (Exception e) {
            log.warn("Logo não encontrada em {} - documento seguirá sem imagem: {}", LOGO_CLASSPATH, e.getMessage());
            return null;
        }
    }
}
