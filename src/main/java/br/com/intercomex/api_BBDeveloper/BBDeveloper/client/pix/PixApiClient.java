package br.com.intercomex.api_BBDeveloper.BBDeveloper.client.pix;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.support.BBClientSupport;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobrancaRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobvRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobrancaImediataDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobvListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobvResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Slf4j
@Component
public class PixApiClient extends BBClientSupport {

    private static final String API = "Pix";

    public PixApiClient(BBApiProperties properties, WebClient bbWebClient) {
        super(properties, bbWebClient);
    }

    public PixCobrancaImediataDTO criarCob(String txid, PixCobrancaRequestDTO request, String token) {
        log.info("Criando cobrança Pix (Cob) — txid: {}", txid);
        return bbWebClient.put()
                .uri(pixUri("/cob/" + txid))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "criar cobrança Cob"))
                .bodyToMono(PixCobrancaImediataDTO.class)
                .doOnSuccess(r -> log.info("Cob criada: txid={}, status={}", r.txid(), r.status()))
                .block();
    }

    public PixCobrancaImediataDTO criarCobSemTxid(PixCobrancaRequestDTO request, String token) {
        log.info("Criando cobrança Pix (Cob) sem txid");
        return bbWebClient.post()
                .uri(pixUri("/cob"))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "criar cobrança Cob sem txid"))
                .bodyToMono(PixCobrancaImediataDTO.class)
                .doOnSuccess(r -> log.info("Cob criada: txid={}, status={}", r.txid(), r.status()))
                .block();
    }

    public PixCobrancaImediataDTO consultarCob(String txid, String token) {
        log.info("Consultando cobrança Pix (Cob) — txid: {}", txid);
        return bbWebClient.get()
                .uri(pixUri("/cob/" + txid))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "consultar cobrança Cob"))
                .bodyToMono(PixCobrancaImediataDTO.class)
                .doOnSuccess(r -> log.info("Cob consultada: txid={}, status={}", r.txid(), r.status()))
                .block();
    }

    public PixCobrancaImediataDTO revisarCob(String txid, PixCobrancaRequestDTO request, String token) {
        log.info("Revisando cobrança Pix (Cob) — txid: {}", txid);
        return bbWebClient.patch()
                .uri(pixUri("/cob/" + txid))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "revisar cobrança Cob"))
                .bodyToMono(PixCobrancaImediataDTO.class)
                .doOnSuccess(r -> log.info("Cob revisada: txid={}, status={}", r.txid(), r.status()))
                .block();
    }

    public PixCobListaResponseDTO listarCobs(
            String token, String inicio, String fim, String cpf, String cnpj, String status,
            Integer paginaAtual, Integer itensPorPagina) {
        log.info("Listando cobranças Pix (Cob) — período: {} a {}", inicio, fim);
        String uri = UriComponentsBuilder.fromUriString(pixUri("/cob"))
                .queryParamIfPresent("inicio", Optional.ofNullable(inicio))
                .queryParamIfPresent("fim", Optional.ofNullable(fim))
                .queryParamIfPresent("cpf", Optional.ofNullable(cpf))
                .queryParamIfPresent("cnpj", Optional.ofNullable(cnpj))
                .queryParamIfPresent("status", Optional.ofNullable(status))
                .queryParamIfPresent("paginacao.paginaAtual", Optional.ofNullable(paginaAtual))
                .queryParamIfPresent("paginacao.itensPorPagina", Optional.ofNullable(itensPorPagina))
                .build()
                .toUriString();

        return bbWebClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "listar cobranças Cob"))
                .bodyToMono(PixCobListaResponseDTO.class)
                .doOnSuccess(r -> log.info("Cobs listadas com sucesso"))
                .block();
    }

    public PixCobvResponseDTO criarCobv(String txid, PixCobvRequestDTO request, String token) {
        log.info("Criando cobrança Pix (CobV) — txid: {}", txid);
        return bbWebClient.put()
                .uri(pixUri("/cobv/" + txid))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "criar cobrança CobV"))
                .bodyToMono(PixCobvResponseDTO.class)
                .doOnSuccess(r -> log.info("CobV criada: txid={}, status={}", r.txid(), r.status()))
                .block();
    }

    public PixCobvResponseDTO criarCobvSemTxid(PixCobvRequestDTO request, String token) {
        log.info("Criando cobrança Pix (CobV) sem txid");
        return bbWebClient.post()
                .uri(pixUri("/cobv"))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "criar cobrança CobV sem txid"))
                .bodyToMono(PixCobvResponseDTO.class)
                .doOnSuccess(r -> log.info("CobV criada: txid={}, status={}", r.txid(), r.status()))
                .block();
    }

    public PixCobvResponseDTO consultarCobv(String txid, String token) {
        log.info("Consultando cobrança Pix (CobV) — txid: {}", txid);
        return bbWebClient.get()
                .uri(pixUri("/cobv/" + txid))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "consultar cobrança CobV"))
                .bodyToMono(PixCobvResponseDTO.class)
                .doOnSuccess(r -> log.info("CobV consultada: txid={}, status={}", r.txid(), r.status()))
                .block();
    }

    public PixCobvResponseDTO revisarCobv(String txid, PixCobvRequestDTO request, String token) {
        log.info("Revisando cobrança Pix (CobV) — txid: {}", txid);
        return bbWebClient.patch()
                .uri(pixUri("/cobv/" + txid))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "revisar cobrança CobV"))
                .bodyToMono(PixCobvResponseDTO.class)
                .doOnSuccess(r -> log.info("CobV revisada: txid={}, status={}", r.txid(), r.status()))
                .block();
    }

    public PixCobvListaResponseDTO listarCobvs(
            String token, String inicio, String fim, String cpf, String cnpj, String status,
            Integer paginaAtual, Integer itensPorPagina) {
        log.info("Listando cobranças Pix (CobV) — período: {} a {}", inicio, fim);
        String uri = UriComponentsBuilder.fromUriString(pixUri("/cobv"))
                .queryParamIfPresent("inicio", Optional.ofNullable(inicio))
                .queryParamIfPresent("fim", Optional.ofNullable(fim))
                .queryParamIfPresent("cpf", Optional.ofNullable(cpf))
                .queryParamIfPresent("cnpj", Optional.ofNullable(cnpj))
                .queryParamIfPresent("status", Optional.ofNullable(status))
                .queryParamIfPresent("paginacao.paginaAtual", Optional.ofNullable(paginaAtual))
                .queryParamIfPresent("paginacao.itensPorPagina", Optional.ofNullable(itensPorPagina))
                .build()
                .toUriString();

        return bbWebClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "listar cobranças CobV"))
                .bodyToMono(PixCobvListaResponseDTO.class)
                .doOnSuccess(r -> log.info("CobVs listadas com sucesso"))
                .block();
    }
}
