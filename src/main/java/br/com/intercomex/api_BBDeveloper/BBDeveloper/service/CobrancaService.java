package br.com.intercomex.api_BBDeveloper.BBDeveloper.service;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.BBApiClient;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.auth.TokenResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.request.BoletoRegistrarRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoPixResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class CobrancaService {

    private final BBApiProperties properties;
    private final BBApiClient bbApiClient;
    private final AuthService authService;

    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final int CAMPO_UTILIZACAO_BENEFICIARIO_MAX_LENGTH = 25;
    private static final String CAMPO_UTILIZACAO_BENEFICIARIO_PADRAO = "SERVICO PRESTADO";

    public BoletoListaResponseDTO listarBoletos(Integer numeroConvenio) {
        log.debug("Listando boletos do convênio: {}", numeroConvenio);

        TokenResponseDTO token = autenticar();

        String dataInicio = LocalDate.now().minusDays(30).format(DATA_FORMATTER);
        String dataFim = LocalDate.now().plusDays(30).format(DATA_FORMATTER);

        return bbApiClient.listarBoletos(token.getAccess_token(), numeroConvenio, dataInicio, dataFim);
    }

    public BoletoResponseDTO registrarBoleto(
            Integer numeroConvenio,
            String nomePagador,
            String cpfCnpjPagador,
            Double valor,
            Integer diasVencimento,
            Boolean comPix
    ) {
        log.debug("Registrando boleto — convênio: {}, pagador: {}, valor: {}", numeroConvenio, nomePagador, valor);

        TokenResponseDTO token = autenticar();

        String dataHoje = LocalDate.now().format(DATA_FORMATTER);
        String dataVencimento = LocalDate.now().plusDays(diasVencimento).format(DATA_FORMATTER);

        // Nosso número: 14 dígitos numéricos únicos
        String numeroTituloCliente = String.format("%014d", System.currentTimeMillis() % 100000000000000L);

        // Define tipo de inscrição: CPF = 1, CNPJ = 2
        int tipoInscricao = cpfCnpjPagador.replaceAll("\\D", "").length() <= 11 ? 1 : 2;

        BoletoRegistrarRequestDTO request = BoletoRegistrarRequestDTO.builder()
                .numeroConvenio(numeroConvenio)
                .numeroCarteira(17)
                .numeroVariacaoCarteira(35)
                .codigoModalidade(1)
                .dataEmissao(dataHoje)
                .dataVencimento(dataVencimento)
                .valorOriginal(valor)
                .indicadorAceiteTituloVencido("N")
                .codigoAceite("A")
                .codigoTipoTitulo(2)
                .descricaoTipoTitulo("DUPLICATA MERCANTIL")
                .indicadorPermissaoRecebimentoParcial("N")
                .numeroTituloBeneficiario("1")
                .campoUtilizacaoBeneficiario(formatarCampoUtilizacaoBeneficiario("Pagamento referente ao serviço prestado"))
                .numeroTituloCliente(numeroTituloCliente)
                .mensagemBloquetoOcorrencia("")
                .indicadorPix(Boolean.TRUE.equals(comPix) ? "S" : "N")
                .pagador(BoletoRegistrarRequestDTO.Pagador.builder()
                        .tipoInscricao(tipoInscricao)
                        .numeroInscricao(cpfCnpjPagador.replaceAll("\\D", ""))
                        .nome(nomePagador)
                        .endereco("Endereço não informado")
                        .cep("70040912")
                        .cidade("Brasília")
                        .bairro("Centro")
                        .uf("DF")
                        .telefone("")
                        .build())
                .build();

        return bbApiClient.registrarBoleto(token.getAccess_token(), request);
    }

    public BoletoPixResponseDTO consultarPixBoleto(String numeroBoleto) {
        log.debug("Consultando Pix do boleto: {}", numeroBoleto);
        TokenResponseDTO token = autenticar();
        return bbApiClient.consultarPixBoleto(token.getAccess_token(), numeroBoleto);
    }

    public BoletoPixResponseDTO gerarPixBoleto(String numeroBoleto) {
        log.debug("Gerando Pix para boleto: {}", numeroBoleto);
        TokenResponseDTO token = autenticar();
        return bbApiClient.gerarPixBoleto(token.getAccess_token(), numeroBoleto);
    }

    public void cancelarPixBoleto(String numeroBoleto) {
        log.debug("Cancelando Pix do boleto: {}", numeroBoleto);
        TokenResponseDTO token = autenticar();
        bbApiClient.cancelarPixBoleto(token.getAccess_token(), numeroBoleto);
    }

    private TokenResponseDTO autenticar() {
        TokenResponseDTO token = authService.gerarToken();
        if (token == null || token.getAccess_token() == null) {
            throw new IllegalStateException("Falha ao obter token de acesso");
        }
        return token;
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
