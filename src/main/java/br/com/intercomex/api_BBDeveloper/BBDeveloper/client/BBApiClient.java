package br.com.intercomex.api_BBDeveloper.BBDeveloper.client;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.auth.TokenResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.request.BoletoRegistrarRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoPixResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobrancaRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobvRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobrancaImediataDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobvListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobvResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class BBApiClient {

    private final BBApiProperties properties;
    private final WebClient bbWebClient;

    public TokenResponseDTO obterToken() {
        log.debug("Solicitando novo token OAuth2.0 ao BB Developer");

        String credentials = properties.getClientId() + ":" + properties.getClientSecret();
        String base64Credentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        return bbWebClient
                .post()
                .uri(properties.getOauthUrl())
                .header("Authorization", "Basic " + base64Credentials)
                .header("gw-dev-app-key", properties.getDeveloperKey())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "client_credentials")
                        .with("scope", properties.getScope()))
                .retrieve()
                .bodyToMono(TokenResponseDTO.class)
                .doOnSuccess(token -> log.info("Token obtido com sucesso. Válido por: {} segundos",
                        token.getExpires_in()))
                .doOnError(error -> log.error("Erro ao obter token: {}", error.getMessage()))
                .block();
    }

    public PixCobrancaImediataDTO criarCobrancaPix(String txid, PixCobrancaRequestDTO request, String token) {
        log.info("Criando cobrança Pix (Cob) — txid: {}", txid);

        return bbWebClient
                .put()
                .uri(pixUri("/cob/" + txid))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, pixErrorHandler("criar cobrança Cob"))
                .bodyToMono(PixCobrancaImediataDTO.class)
                .doOnSuccess(r -> log.info("Cob criada: txid={}, status={}", r.getTxid(), r.getStatus()))
                .doOnError(e -> log.error("Erro ao criar cobrança Cob: {}", e.getMessage()))
                .block();
    }

    public PixCobrancaImediataDTO criarCobrancaPixSemTxid(PixCobrancaRequestDTO request, String token) {
        log.info("Criando cobrança Pix (Cob) sem txid");

        return bbWebClient
                .post()
                .uri(pixUri("/cob"))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, pixErrorHandler("criar cobrança Cob sem txid"))
                .bodyToMono(PixCobrancaImediataDTO.class)
                .doOnSuccess(r -> log.info("Cob criada: txid={}, status={}", r.getTxid(), r.getStatus()))
                .doOnError(e -> log.error("Erro ao criar cobrança Cob sem txid: {}", e.getMessage()))
                .block();
    }

    public PixCobrancaImediataDTO consultarCobrancaPix(String txid, String token) {
        log.info("Consultando cobrança Pix (Cob) — txid: {}", txid);

        return bbWebClient
                .get()
                .uri(pixUri("/cob/" + txid))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, pixErrorHandler("consultar cobrança Cob"))
                .bodyToMono(PixCobrancaImediataDTO.class)
                .doOnSuccess(r -> log.info("Cob consultada: txid={}, status={}", r.getTxid(), r.getStatus()))
                .doOnError(e -> log.error("Erro ao consultar cobrança Cob: {}", e.getMessage()))
                .block();
    }

    public PixCobrancaImediataDTO revisarCobrancaPix(String txid, PixCobrancaRequestDTO request, String token) {
        log.info("Revisando cobrança Pix (Cob) — txid: {}", txid);

        return bbWebClient
                .patch()
                .uri(pixUri("/cob/" + txid))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, pixErrorHandler("revisar cobrança Cob"))
                .bodyToMono(PixCobrancaImediataDTO.class)
                .doOnSuccess(r -> log.info("Cob revisada: txid={}, status={}", r.getTxid(), r.getStatus()))
                .doOnError(e -> log.error("Erro ao revisar cobrança Cob: {}", e.getMessage()))
                .block();
    }

    public PixCobListaResponseDTO listarCobrancasPix(
            String token, String inicio, String fim, String cpf, String cnpj, String status,
            Integer paginaAtual, Integer itensPorPagina) {
        log.info("Listando cobranças Pix (Cob) — período: {} a {}", inicio, fim);

        String uri = UriComponentsBuilder
                .fromUriString(pixUri("/cob"))
                .queryParamIfPresent("inicio", java.util.Optional.ofNullable(inicio))
                .queryParamIfPresent("fim", java.util.Optional.ofNullable(fim))
                .queryParamIfPresent("cpf", java.util.Optional.ofNullable(cpf))
                .queryParamIfPresent("cnpj", java.util.Optional.ofNullable(cnpj))
                .queryParamIfPresent("status", java.util.Optional.ofNullable(status))
                .queryParamIfPresent("paginacao.paginaAtual", java.util.Optional.ofNullable(paginaAtual))
                .queryParamIfPresent("paginacao.itensPorPagina", java.util.Optional.ofNullable(itensPorPagina))
                .build()
                .toUriString();

        return bbWebClient
                .get()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, pixErrorHandler("listar cobranças Cob"))
                .bodyToMono(PixCobListaResponseDTO.class)
                .doOnSuccess(r -> log.info("Cobs listadas com sucesso"))
                .doOnError(e -> log.error("Erro ao listar cobranças Cob: {}", e.getMessage()))
                .block();
    }

    public PixCobvResponseDTO criarCobvPix(String txid, PixCobvRequestDTO request, String token) {
        log.info("Criando cobrança Pix (CobV) — txid: {}", txid);

        return bbWebClient
                .put()
                .uri(pixUri("/cobv/" + txid))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, pixErrorHandler("criar cobrança CobV"))
                .bodyToMono(PixCobvResponseDTO.class)
                .doOnSuccess(r -> log.info("CobV criada: txid={}, status={}", r.getTxid(), r.getStatus()))
                .doOnError(e -> log.error("Erro ao criar cobrança CobV: {}", e.getMessage()))
                .block();
    }

    public PixCobvResponseDTO criarCobvPixSemTxid(PixCobvRequestDTO request, String token) {
        log.info("Criando cobrança Pix (CobV) sem txid");

        return bbWebClient
                .post()
                .uri(pixUri("/cobv"))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, pixErrorHandler("criar cobrança CobV sem txid"))
                .bodyToMono(PixCobvResponseDTO.class)
                .doOnSuccess(r -> log.info("CobV criada: txid={}, status={}", r.getTxid(), r.getStatus()))
                .doOnError(e -> log.error("Erro ao criar cobrança CobV sem txid: {}", e.getMessage()))
                .block();
    }

    public PixCobvResponseDTO consultarCobvPix(String txid, String token) {
        log.info("Consultando cobrança Pix (CobV) — txid: {}", txid);

        return bbWebClient
                .get()
                .uri(pixUri("/cobv/" + txid))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, pixErrorHandler("consultar cobrança CobV"))
                .bodyToMono(PixCobvResponseDTO.class)
                .doOnSuccess(r -> log.info("CobV consultada: txid={}, status={}", r.getTxid(), r.getStatus()))
                .doOnError(e -> log.error("Erro ao consultar cobrança CobV: {}", e.getMessage()))
                .block();
    }

    public PixCobvResponseDTO revisarCobvPix(String txid, PixCobvRequestDTO request, String token) {
        log.info("Revisando cobrança Pix (CobV) — txid: {}", txid);

        return bbWebClient
                .patch()
                .uri(pixUri("/cobv/" + txid))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, pixErrorHandler("revisar cobrança CobV"))
                .bodyToMono(PixCobvResponseDTO.class)
                .doOnSuccess(r -> log.info("CobV revisada: txid={}, status={}", r.getTxid(), r.getStatus()))
                .doOnError(e -> log.error("Erro ao revisar cobrança CobV: {}", e.getMessage()))
                .block();
    }

    public PixCobvListaResponseDTO listarCobvPix(
            String token, String inicio, String fim, String cpf, String cnpj, String status,
            Integer paginaAtual, Integer itensPorPagina) {
        log.info("Listando cobranças Pix (CobV) — período: {} a {}", inicio, fim);

        String uri = UriComponentsBuilder
                .fromUriString(pixUri("/cobv"))
                .queryParamIfPresent("inicio", java.util.Optional.ofNullable(inicio))
                .queryParamIfPresent("fim", java.util.Optional.ofNullable(fim))
                .queryParamIfPresent("cpf", java.util.Optional.ofNullable(cpf))
                .queryParamIfPresent("cnpj", java.util.Optional.ofNullable(cnpj))
                .queryParamIfPresent("status", java.util.Optional.ofNullable(status))
                .queryParamIfPresent("paginacao.paginaAtual", java.util.Optional.ofNullable(paginaAtual))
                .queryParamIfPresent("paginacao.itensPorPagina", java.util.Optional.ofNullable(itensPorPagina))
                .build()
                .toUriString();

        return bbWebClient
                .get()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, pixErrorHandler("listar cobranças CobV"))
                .bodyToMono(PixCobvListaResponseDTO.class)
                .doOnSuccess(r -> log.info("CobVs listadas com sucesso"))
                .doOnError(e -> log.error("Erro ao listar cobranças CobV: {}", e.getMessage()))
                .block();
    }

    public BoletoListaResponseDTO listarBoletos(String token, Integer numeroConvenio,
                                                 String dataInicio, String dataFim) {
        log.info("Listando boletos — convênio: {}, período: {} a {}", numeroConvenio, dataInicio, dataFim);

        return bbWebClient
                .get()
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
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class).flatMap(body -> {
                            log.error("Erro listar boletos BB — Status: {} Body: {}", response.statusCode(), body);
                            return Mono.error(new RuntimeException("Erro BB API Cobrança: " + body));
                        }))
                .bodyToMono(BoletoListaResponseDTO.class)
                .doOnSuccess(r -> log.info("Boletos listados com sucesso"))
                .doOnError(e -> log.error("Erro ao listar boletos: {}", e.getMessage()))
                .block();
    }

    public BoletoResponseDTO registrarBoleto(String token, BoletoRegistrarRequestDTO request) {
        log.info("Registrando boleto — convênio: {}, valor: {}", request.getNumeroConvenio(), request.getValorOriginal());

        return bbWebClient
                .post()
                .uri(properties.getCobrancaBaseUrl() + "/boletos"
                        + "?gw-dev-app-key=" + properties.getDeveloperKey())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class).flatMap(body -> {
                            log.error("Erro registrar boleto BB — Status: {} Body: {}", response.statusCode(), body);
                            return Mono.error(new RuntimeException("Erro BB API Cobrança: " + body));
                        }))
                .bodyToMono(BoletoResponseDTO.class)
                .doOnSuccess(r -> log.info("Boleto registrado: numero={}", r.getNumero()))
                .doOnError(e -> log.error("Erro ao registrar boleto: {}", e.getMessage()))
                .block();
    }

    public BoletoPixResponseDTO consultarPixBoleto(String token, String numeroBoleto) {
        log.info("Consultando Pix do boleto: {}", numeroBoleto);

        return bbWebClient
                .get()
                .uri(properties.getCobrancaBaseUrl() + "/boletos/" + numeroBoleto + "/pix"
                        + "?gw-dev-app-key=" + properties.getDeveloperKey())
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class).flatMap(body -> {
                            log.error("Erro consultar Pix boleto — Status: {} Body: {}", response.statusCode(), body);
                            return Mono.error(new RuntimeException("Erro BB API Cobrança Pix: " + body));
                        }))
                .bodyToMono(BoletoPixResponseDTO.class)
                .doOnSuccess(r -> log.info("Pix do boleto consultado: txId={}", r.getTxId()))
                .doOnError(e -> log.error("Erro ao consultar Pix do boleto: {}", e.getMessage()))
                .block();
    }

    public BoletoPixResponseDTO gerarPixBoleto(String token, String numeroBoleto) {
        log.info("Gerando Pix para boleto: {}", numeroBoleto);

        return bbWebClient
                .post()
                .uri(properties.getCobrancaBaseUrl() + "/boletos/" + numeroBoleto + "/pix"
                        + "?gw-dev-app-key=" + properties.getDeveloperKey())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class).flatMap(body -> {
                            log.error("Erro gerar Pix boleto — Status: {} Body: {}", response.statusCode(), body);
                            return Mono.error(new RuntimeException("Erro BB API Cobrança Pix: " + body));
                        }))
                .bodyToMono(BoletoPixResponseDTO.class)
                .doOnSuccess(r -> log.info("Pix gerado para boleto: txId={}", r.getTxId()))
                .doOnError(e -> log.error("Erro ao gerar Pix do boleto: {}", e.getMessage()))
                .block();
    }

    public void cancelarPixBoleto(String token, String numeroBoleto) {
        log.info("Cancelando Pix do boleto: {}", numeroBoleto);

        bbWebClient
                .delete()
                .uri(properties.getCobrancaBaseUrl() + "/boletos/" + numeroBoleto + "/pix"
                        + "?gw-dev-app-key=" + properties.getDeveloperKey())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class).flatMap(body -> {
                            log.error("Erro cancelar Pix boleto — Status: {} Body: {}", response.statusCode(), body);
                            return Mono.error(new RuntimeException("Erro BB API Cobrança Pix: " + body));
                        }))
                .bodyToMono(Void.class)
                .doOnSuccess(r -> log.info("Pix do boleto cancelado com sucesso"))
                .doOnError(e -> log.error("Erro ao cancelar Pix do boleto: {}", e.getMessage()))
                .block();
    }

    private String pixUri(String path) {
        return properties.getPixBaseUrl() + path + "?gw-dev-app-key=" + properties.getDeveloperKey();
    }

    private Function<org.springframework.web.reactive.function.client.ClientResponse, Mono<? extends Throwable>> pixErrorHandler(String operacao) {
        return response -> response.bodyToMono(String.class).flatMap(body -> {
            log.error("Erro Pix BB ({}) — Status: {} Body: {}", operacao, response.statusCode(), body);
            return Mono.error(new RuntimeException("Erro BB API Pix: " + body));
        });
    }
}
