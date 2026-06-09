package br.com.intercomex.api_BBDeveloper.BBDeveloper.client.cobranca;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.support.BBClientSupport;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.request.BoletoRegistrarRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoPixResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class CobrancaApiClient extends BBClientSupport {

    private static final String API = "Cobrança";

    public CobrancaApiClient(BBApiProperties properties, WebClient bbWebClient) {
        super(properties, bbWebClient);
    }

    public BoletoListaResponseDTO listarBoletos(String token, Integer numeroConvenio,
                                                String dataInicio, String dataFim) {
        log.info("Listando boletos — convênio: {}, período: {} a {}", numeroConvenio, dataInicio, dataFim);

        return bbWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(properties.getCobrancaBaseUrl() + "/boletos")
                        .queryParam("gw-dev-app-key", properties.getDeveloperKey())
                        .queryParam("numeroConvenio", numeroConvenio)
                        .queryParam("indicadorSituacao", "A")
                        .queryParam("dataInicioVencimento", dataInicio)
                        .queryParam("dataFimVencimento", dataFim)
                        .build())
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

    public BoletoPixResponseDTO consultarPixBoleto(String token, String numeroBoleto) {
        log.info("Consultando Pix do boleto: {}", numeroBoleto);

        return bbWebClient.get()
                .uri(cobrancaUri("/boletos/" + numeroBoleto + "/pix"))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "consultar Pix boleto"))
                .bodyToMono(BoletoPixResponseDTO.class)
                .doOnSuccess(r -> log.info("Pix do boleto consultado: txId={}", r.txId()))
                .block();
    }

    public BoletoPixResponseDTO gerarPixBoleto(String token, String numeroBoleto) {
        log.info("Gerando Pix para boleto: {}", numeroBoleto);

        return bbWebClient.post()
                .uri(cobrancaUri("/boletos/" + numeroBoleto + "/pix"))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "gerar Pix boleto"))
                .bodyToMono(BoletoPixResponseDTO.class)
                .doOnSuccess(r -> log.info("Pix gerado para boleto: txId={}", r.txId()))
                .block();
    }

    public void cancelarPixBoleto(String token, String numeroBoleto) {
        log.info("Cancelando Pix do boleto: {}", numeroBoleto);

        bbWebClient.delete()
                .uri(cobrancaUri("/boletos/" + numeroBoleto + "/pix"))
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "cancelar Pix boleto"))
                .bodyToMono(Void.class)
                .doOnSuccess(r -> log.info("Pix do boleto cancelado com sucesso"))
                .block();
    }
}
