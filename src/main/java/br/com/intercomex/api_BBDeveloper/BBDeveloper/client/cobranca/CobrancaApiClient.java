package br.com.intercomex.api_BBDeveloper.BBDeveloper.client.cobranca;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.support.BBClientSupport;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.request.BoletoAlterarRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.request.BoletoPixOperacaoRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.request.BoletoRegistrarRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoBaixaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoConsultaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoPixResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class CobrancaApiClient extends BBClientSupport {

    private static final String API = "Cobrança";

    public CobrancaApiClient(BBApiProperties properties, @Qualifier("bbWebClient") WebClient bbWebClient) {
        super(properties, bbWebClient);
    }

    public BoletoListaResponseDTO listarBoletos(String token, Integer numeroConvenio,
                                                String agenciaBeneficiario, String contaBeneficiario,
                                                String dataInicio, String dataFim) {
        log.info("Listando boletos — convênio: {}, agência: {}, conta: {}, período: {} a {}",
                numeroConvenio, agenciaBeneficiario, contaBeneficiario, dataInicio, dataFim);

        String uri = UriComponentsBuilder.fromUriString(properties.getCobrancaBaseUrl() + "/boletos")
                .queryParam("gw-dev-app-key", properties.getDeveloperKey())
                .queryParam("numeroConvenio", numeroConvenio)
                .queryParam("agenciaBeneficiario", agenciaBeneficiario)
                .queryParam("contaBeneficiario", contaBeneficiario)
                .queryParam("indicadorSituacao", "A")
                .queryParam("dataInicioVencimento", dataInicio)
                .queryParam("dataFimVencimento", dataFim)
                .build()
                .toUriString();

        return bbWebClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "listar boletos"))
                .bodyToMono(BoletoListaResponseDTO.class)
                .doOnSuccess(r -> log.info("Boletos listados com sucesso"))
                .block();
    }

    public BoletoResponseDTO registrarBoleto(String token, BoletoRegistrarRequestDTO request) {
        log.info("Registrando boleto — convênio: {}, valor: {}", request.numeroConvenio(), request.valorOriginal());

        return bbWebClient.post()
                .uri(cobrancaUri("/boletos"))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "registrar boleto"))
                .bodyToMono(BoletoResponseDTO.class)
                .doOnSuccess(r -> log.info("Boleto registrado: numero={}", r.numero()))
                .block();
    }

    public BoletoPixResponseDTO consultarPixBoleto(String token, String numeroBoleto, Integer numeroConvenio) {
        log.info("Consultando Pix do boleto: {} (convênio: {})", numeroBoleto, numeroConvenio);

        return bbWebClient.get()
                .uri(boletoPixUri(numeroBoleto, numeroConvenio))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "consultar Pix boleto"))
                .bodyToMono(BoletoPixResponseDTO.class)
                .doOnSuccess(r -> log.info("Pix do boleto consultado: txId={}", r.txId()))
                .block();
    }

    public BoletoPixResponseDTO gerarPixBoleto(String token, String numeroBoleto, Integer numeroConvenio) {
        log.info("Gerando Pix para boleto: {} (convênio: {})", numeroBoleto, numeroConvenio);

        return bbWebClient.post()
                .uri(boletoPixOperacaoUri(numeroBoleto, numeroConvenio, "gerar-pix"))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(new BoletoPixOperacaoRequestDTO(numeroConvenio))
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "gerar Pix boleto"))
                .bodyToMono(BoletoPixResponseDTO.class)
                .doOnSuccess(r -> log.info("Pix gerado para boleto: txId={}", r.txId()))
                .block();
    }

    public BoletoConsultaResponseDTO consultarBoleto(String token, String numeroBoleto, Integer numeroConvenio) {
        log.info("Consultando boleto: {} (convênio: {})", numeroBoleto, numeroConvenio);

        BoletoConsultaResponseDTO resposta = bbWebClient.get()
                .uri(boletoOperacaoUri(numeroBoleto, numeroConvenio))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "consultar boleto"))
                .bodyToMono(BoletoConsultaResponseDTO.class)
                .block();

        if (resposta == null) {
            return null;
        }

        if (resposta.numero() == null || resposta.numero().isBlank()) {
            return new BoletoConsultaResponseDTO(
                    numeroBoleto,
                    resposta.codigoLinhaDigitavel(),
                    resposta.textoCodigoBarrasTituloCobranca(),
                    resposta.numeroContratoCobranca(),
                    resposta.dataEmissaoTituloCobranca(),
                    resposta.dataVencimentoTituloCobranca(),
                    resposta.dataRegistroTituloCobranca(),
                    resposta.valorOriginalTituloCobranca(),
                    resposta.valorAtualTituloCobranca(),
                    resposta.codigoEstadoTituloCobranca(),
                    resposta.numeroCarteiraCobranca(),
                    resposta.numeroVariacaoCarteiraCobranca(),
                    resposta.nomeSacadoCobranca(),
                    resposta.numeroInscricaoSacadoCobranca(),
                    resposta.textoCampoUtilizacaoCedente(),
                    resposta.dataRecebimentoTitulo(),
                    resposta.valorPagoSacado()
            );
        }

        return resposta;
    }

    public BoletoResponseDTO alterarBoleto(
            String token, String numeroBoleto, BoletoAlterarRequestDTO request) {
        log.info("Alterando boleto: {} (convênio: {})", numeroBoleto, request.numeroConvenio());

        return bbWebClient.patch()
                .uri(cobrancaUri("/boletos/" + numeroBoleto))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "alterar boleto"))
                .bodyToMono(BoletoResponseDTO.class)
                .doOnSuccess(r -> log.info("Boleto alterado: numero={}", r.numero()))
                .block();
    }

    public BoletoBaixaResponseDTO baixarBoleto(String token, String numeroBoleto, Integer numeroConvenio) {
        log.info("Baixando boleto: {} (convênio: {})", numeroBoleto, numeroConvenio);

        return bbWebClient.post()
                .uri(cobrancaUri("/boletos/" + numeroBoleto + "/baixar"))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(new BoletoPixOperacaoRequestDTO(numeroConvenio))
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "baixar boleto"))
                .bodyToMono(BoletoBaixaResponseDTO.class)
                .doOnSuccess(r -> log.info("Baixa de boleto solicitada: codigoErro={}", r.codigoErroRegistro()))
                .block();
    }

    public void cancelarPixBoleto(String token, String numeroBoleto, Integer numeroConvenio) {
        log.info("Cancelando Pix do boleto: {} (convênio: {})", numeroBoleto, numeroConvenio);

        bbWebClient.post()
                .uri(boletoPixOperacaoUri(numeroBoleto, numeroConvenio, "cancelar-pix"))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BoletoPixOperacaoRequestDTO(numeroConvenio))
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "cancelar Pix boleto"))
                .bodyToMono(Void.class)
                .doOnSuccess(r -> log.info("Pix do boleto cancelado com sucesso"))
                .block();
    }

    private String boletoOperacaoUri(String numeroBoleto, Integer numeroConvenio) {
        return UriComponentsBuilder
                .fromUriString(properties.getCobrancaBaseUrl() + "/boletos/" + numeroBoleto)
                .queryParam("gw-dev-app-key", properties.getDeveloperKey())
                .queryParam("numeroConvenio", numeroConvenio)
                .build()
                .toUriString();
    }

    private String boletoPixUri(String numeroBoleto, Integer numeroConvenio) {
        return UriComponentsBuilder
                .fromUriString(properties.getCobrancaBaseUrl() + "/boletos/" + numeroBoleto + "/pix")
                .queryParam("gw-dev-app-key", properties.getDeveloperKey())
                .queryParam("numeroConvenio", numeroConvenio)
                .build()
                .toUriString();
    }

    private String boletoPixOperacaoUri(String numeroBoleto, Integer numeroConvenio, String operacao) {
        return UriComponentsBuilder
                .fromUriString(properties.getCobrancaBaseUrl() + "/boletos/" + numeroBoleto + "/" + operacao)
                .queryParam("gw-dev-app-key", properties.getDeveloperKey())
                .queryParam("numeroConvenio", numeroConvenio)
                .build()
                .toUriString();
    }
}
