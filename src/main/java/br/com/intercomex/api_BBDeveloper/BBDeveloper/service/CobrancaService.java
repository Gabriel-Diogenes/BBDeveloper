package br.com.intercomex.api_BBDeveloper.BBDeveloper.service;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.cobranca.CobrancaApiClient;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.auth.TokenResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.request.BoletoAlterarRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.request.BoletoRegistrarRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoBaixaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoConsultaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoPixResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.util.ExtratoUtil;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class CobrancaService {

    private final CobrancaApiClient cobrancaApiClient;
    private final AuthService authService;

    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final int CAMPO_UTILIZACAO_BENEFICIARIO_MAX_LENGTH = 25;
    private static final String CAMPO_UTILIZACAO_BENEFICIARIO_PADRAO = "SERVICO PRESTADO";

    public BoletoListaResponseDTO listarBoletos(
            Integer numeroConvenio, String agenciaBeneficiario, String contaBeneficiario) {
        log.debug("Listando boletos do convênio: {} (agência: {}, conta: {})",
                numeroConvenio, agenciaBeneficiario, contaBeneficiario);

        String dataInicio = LocalDate.now().minusDays(30).format(DATA_FORMATTER);
        String dataFim = LocalDate.now().plusDays(30).format(DATA_FORMATTER);

        String agencia = ExtratoUtil.formatarAgencia(agenciaBeneficiario);
        String conta = ExtratoUtil.formatarConta(contaBeneficiario);

        return cobrancaApiClient.listarBoletos(
                bearer(), numeroConvenio, agencia, conta, dataInicio, dataFim);
    }

    public BoletoResponseDTO registrarBoleto(BoletoRegistrarRequestDTO request) {
        BoletoRegistrarRequestDTO normalizado = normalizarRegistro(request);
        log.debug("Registrando boleto — convênio: {}, valor: {}",
                normalizado.numeroConvenio(), normalizado.valorOriginal());
        return cobrancaApiClient.registrarBoleto(bearer(), normalizado);
    }

    public BoletoResponseDTO registrarBoletoSimplificado(
            Integer numeroConvenio,
            String nomePagador,
            String cpfCnpjPagador,
            Double valor,
            Integer diasVencimento,
            Boolean comPix
    ) {
        log.debug("Registrando boleto — convênio: {}, pagador: {}, valor: {}", numeroConvenio, nomePagador, valor);

        String dataHoje = LocalDate.now().format(DATA_FORMATTER);
        String dataVencimento = LocalDate.now().plusDays(diasVencimento).format(DATA_FORMATTER);

        // Nosso número (numeroTituloCliente): 20 dígitos = "000" + convênio (7) + sequencial (10)
        long sequencial = System.currentTimeMillis() % 10000000000L;
        String numeroTituloCliente = String.format("000%07d%010d", numeroConvenio, sequencial);

        // Define tipo de inscrição: CPF = 1, CNPJ = 2
        int tipoInscricao = cpfCnpjPagador.replaceAll("\\D", "").length() <= 11 ? 1 : 2;

        BoletoRegistrarRequestDTO request = new BoletoRegistrarRequestDTO(
                numeroConvenio,
                17,
                35,
                1,
                dataHoje,
                dataVencimento,
                valor,
                "N",
                "A",
                2,
                "DUPLICATA MERCANTIL",
                "N",
                "1",
                formatarCampoUtilizacaoBeneficiario("Pagamento referente ao serviço prestado"),
                numeroTituloCliente,
                "",
                Boolean.TRUE.equals(comPix) ? "S" : "N",
                new BoletoRegistrarRequestDTO.Pagador(
                        tipoInscricao,
                        cpfCnpjPagador.replaceAll("\\D", ""),
                        nomePagador,
                        "Endereço não informado",
                        "70040912",
                        "Brasília",
                        "Centro",
                        "DF",
                        ""
                )
        );

        return registrarBoleto(request);
    }

    public BoletoConsultaResponseDTO consultarBoleto(String numeroBoleto, Integer numeroConvenio) {
        log.debug("Consultando boleto: {} (convênio: {})", numeroBoleto, numeroConvenio);
        return cobrancaApiClient.consultarBoleto(bearer(), numeroBoleto, numeroConvenio);
    }

    public BoletoResponseDTO alterarBoleto(String numeroBoleto, BoletoAlterarRequestDTO request) {
        log.debug("Alterando boleto: {} (convênio: {})", numeroBoleto, request.numeroConvenio());
        return cobrancaApiClient.alterarBoleto(bearer(), numeroBoleto, request);
    }

    public BoletoBaixaResponseDTO baixarBoleto(String numeroBoleto, Integer numeroConvenio) {
        log.debug("Baixando boleto: {} (convênio: {})", numeroBoleto, numeroConvenio);
        return cobrancaApiClient.baixarBoleto(bearer(), numeroBoleto, numeroConvenio);
    }

    public BoletoBaixaResponseDTO cancelarBoleto(String numeroBoleto, Integer numeroConvenio) {
        log.debug("Cancelando boleto: {} (convênio: {}) — BB usa endpoint /baixar", numeroBoleto, numeroConvenio);
        return cobrancaApiClient.baixarBoleto(bearer(), numeroBoleto, numeroConvenio);
    }

    public BoletoPixResponseDTO consultarPixBoleto(String numeroBoleto, Integer numeroConvenio) {
        log.debug("Consultando Pix do boleto: {} (convênio: {})", numeroBoleto, numeroConvenio);
        return cobrancaApiClient.consultarPixBoleto(bearer(), numeroBoleto, numeroConvenio);
    }

    public BoletoPixResponseDTO gerarPixBoleto(String numeroBoleto, Integer numeroConvenio) {
        log.debug("Gerando Pix para boleto: {} (convênio: {})", numeroBoleto, numeroConvenio);
        return cobrancaApiClient.gerarPixBoleto(bearer(), numeroBoleto, numeroConvenio);
    }

    public void cancelarPixBoleto(String numeroBoleto, Integer numeroConvenio) {
        log.debug("Cancelando Pix do boleto: {} (convênio: {})", numeroBoleto, numeroConvenio);
        cobrancaApiClient.cancelarPixBoleto(bearer(), numeroBoleto, numeroConvenio);
    }

    private String bearer() {
        TokenResponseDTO token = authService.gerarToken();
        if (token.accessToken() == null) {
            throw new IllegalStateException("Falha ao obter token de acesso");
        }
        return token.accessToken();
    }

    private BoletoRegistrarRequestDTO normalizarRegistro(BoletoRegistrarRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Body do boleto é obrigatório.");
        }
        if (request.numeroConvenio() == null) {
            throw new IllegalArgumentException("numeroConvenio é obrigatório.");
        }
        if (request.valorOriginal() == null || request.valorOriginal() <= 0) {
            throw new IllegalArgumentException("valorOriginal deve ser maior que zero.");
        }
        if (request.dataEmissao() == null || request.dataEmissao().isBlank()) {
            throw new IllegalArgumentException("dataEmissao é obrigatória (formato dd.MM.yyyy).");
        }
        if (request.dataVencimento() == null || request.dataVencimento().isBlank()) {
            throw new IllegalArgumentException("dataVencimento é obrigatória (formato dd.MM.yyyy).");
        }
        if (request.pagador() == null) {
            throw new IllegalArgumentException("pagador é obrigatório.");
        }

        BoletoRegistrarRequestDTO.Pagador pagador = request.pagador();
        if (pagador.nome() == null || pagador.nome().isBlank()) {
            throw new IllegalArgumentException("pagador.nome é obrigatório.");
        }
        if (pagador.numeroInscricao() == null || pagador.numeroInscricao().isBlank()) {
            throw new IllegalArgumentException("pagador.numeroInscricao é obrigatório.");
        }

        String inscricao = pagador.numeroInscricao().replaceAll("\\D", "");
        Integer tipoInscricao = pagador.tipoInscricao() != null
                ? pagador.tipoInscricao()
                : (inscricao.length() <= 11 ? 1 : 2);

        BoletoRegistrarRequestDTO.Pagador pagadorNormalizado = new BoletoRegistrarRequestDTO.Pagador(
                tipoInscricao,
                inscricao,
                pagador.nome(),
                pagador.endereco() != null ? pagador.endereco() : "Endereço não informado",
                pagador.cep() != null ? pagador.cep().replaceAll("\\D", "") : "70040912",
                pagador.cidade() != null ? pagador.cidade() : "Brasília",
                pagador.bairro() != null ? pagador.bairro() : "Centro",
                pagador.uf() != null ? pagador.uf() : "DF",
                pagador.telefone() != null ? pagador.telefone() : ""
        );

        String numeroTituloCliente = request.numeroTituloCliente();
        if (numeroTituloCliente == null || numeroTituloCliente.isBlank()) {
            long sequencial = System.currentTimeMillis() % 10000000000L;
            numeroTituloCliente = String.format("000%07d%010d", request.numeroConvenio(), sequencial);
        }

        return new BoletoRegistrarRequestDTO(
                request.numeroConvenio(),
                request.numeroCarteira() != null ? request.numeroCarteira() : 17,
                request.numeroVariacaoCarteira() != null ? request.numeroVariacaoCarteira() : 35,
                request.codigoModalidade() != null ? request.codigoModalidade() : 1,
                request.dataEmissao(),
                request.dataVencimento(),
                request.valorOriginal(),
                request.indicadorAceiteTituloVencido() != null ? request.indicadorAceiteTituloVencido() : "N",
                request.codigoAceite() != null ? request.codigoAceite() : "A",
                request.codigoTipoTitulo() != null ? request.codigoTipoTitulo() : 2,
                request.descricaoTipoTitulo() != null ? request.descricaoTipoTitulo() : "DUPLICATA MERCANTIL",
                request.indicadorPermissaoRecebimentoParcial() != null ? request.indicadorPermissaoRecebimentoParcial() : "N",
                request.numeroTituloBeneficiario() != null ? request.numeroTituloBeneficiario() : "1",
                formatarCampoUtilizacaoBeneficiario(request.campoUtilizacaoBeneficiario()),
                numeroTituloCliente,
                request.mensagemBloquetoOcorrencia() != null ? request.mensagemBloquetoOcorrencia() : "",
                request.indicadorPix() != null ? request.indicadorPix() : "S",
                pagadorNormalizado
        );
    }

    private String formatarCampoUtilizacaoBeneficiario(String texto) {
        if (texto == null || texto.isBlank()) {
            return CAMPO_UTILIZACAO_BENEFICIARIO_PADRAO;
        }

        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9 ]", " ")
                .trim()
                .replaceAll(" {2,}", " ")
                .toUpperCase();

        if (normalizado.isBlank()) {
            return CAMPO_UTILIZACAO_BENEFICIARIO_PADRAO;
        }

        if (normalizado.length() > CAMPO_UTILIZACAO_BENEFICIARIO_MAX_LENGTH) {
            log.warn("CampoUtilizacaoBeneficiario maior que {} caracteres ou com caracteres inválidos, truncando.", CAMPO_UTILIZACAO_BENEFICIARIO_MAX_LENGTH);
            normalizado = normalizado.substring(0, CAMPO_UTILIZACAO_BENEFICIARIO_MAX_LENGTH).trim();
        }

        if (normalizado.isBlank()) {
            return CAMPO_UTILIZACAO_BENEFICIARIO_PADRAO;
        }

        return normalizado;
    }
}
