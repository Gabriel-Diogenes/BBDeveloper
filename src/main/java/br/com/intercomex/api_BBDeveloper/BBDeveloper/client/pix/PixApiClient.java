package br.com.intercomex.api_BBDeveloper.BBDeveloper.client.pix;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.support.BBClientSupport;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobrancaRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobvRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixDevolucaoRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobrancaImediataDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobvListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobvResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixDevolucaoDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixRecebidoDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixRecebidoListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Slf4j
@Component
public class PixApiClient extends BBClientSupport {

    private static final String API = "Pix";

    public PixApiClient(
            BBApiProperties properties,
            @Qualifier("bbWebClient") WebClient bbWebClient,
            @Qualifier("bbMtlsWebClient") WebClient bbMtlsWebClient) {
        super(properties, properties.isPixRequerMtls() ? bbMtlsWebClient : bbWebClient);
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

    public PixRecebidoListaResponseDTO listarPixRecebidos(
            String token, String inicio, String fim, String txid, Boolean txIdPresente,
            Boolean devolucaoPresente, String cpf, String cnpj, Integer paginaAtual, Integer itensPorPagina) {
        log.info("Listando Pix recebidos — período: {} a {}", inicio, fim);
        String uri = UriComponentsBuilder.fromUriString(pixUri("/pix"))
                .queryParamIfPresent("inicio", Optional.ofNullable(inicio))
                .queryParamIfPresent("fim", Optional.ofNullable(fim))
                .queryParamIfPresent("txid", Optional.ofNullable(txid))
                .queryParamIfPresent("txIdPresente", Optional.ofNullable(txIdPresente))
                .queryParamIfPresent("devolucaoPresente", Optional.ofNullable(devolucaoPresente))
                .queryParamIfPresent("cpf", Optional.ofNullable(cpf))
                .queryParamIfPresent("cnpj", Optional.ofNullable(cnpj))
                .queryParamIfPresent("paginacao.paginaAtual", Optional.ofNullable(paginaAtual))
                .queryParamIfPresent("paginacao.itensPorPagina", Optional.ofNullable(itensPorPagina))
                .build()
                .toUriString();

        return bbWebClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "listar Pix recebidos"))
                .bodyToMono(PixRecebidoListaResponseDTO.class)
                .doOnSuccess(r -> log.info("Pix recebidos listados com sucesso"))
                .block();
    }

    public PixRecebidoDTO consultarPixRecebido(String e2eid, String token) {
        log.info("Consultando Pix recebido — e2eid: {}", e2eid);
        return bbWebClient.get()
                .uri(pixUri("/pix/" + e2eid))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "consultar Pix recebido"))
                .bodyToMono(PixRecebidoDTO.class)
                .doOnSuccess(r -> log.info("Pix recebido consultado: e2eid={}, valor={}", r.endToEndId(), r.valor()))
                .block();
    }

    public PixDevolucaoDTO solicitarDevolucao(
            String e2eid, String id, PixDevolucaoRequestDTO request, String token) {
        log.info("Solicitando devolução Pix — e2eid: {}, id: {}", e2eid, id);
        return bbWebClient.put()
                .uri(pixUri("/pix/" + e2eid + "/devolucao/" + id))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "solicitar devolução Pix"))
                .bodyToMono(PixDevolucaoDTO.class)
                .doOnSuccess(r -> log.info("Devolução solicitada: id={}, status={}", r.id(), r.status()))
                .block();
    }

    public PixDevolucaoDTO consultarDevolucao(String e2eid, String id, String token) {
        log.info("Consultando devolução Pix — e2eid: {}, id: {}", e2eid, id);
        return bbWebClient.get()
                .uri(pixUri("/pix/" + e2eid + "/devolucao/" + id))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "consultar devolução Pix"))
                .bodyToMono(PixDevolucaoDTO.class)
                .doOnSuccess(r -> log.info("Devolução consultada: id={}, status={}", r.id(), r.status()))
                .block();
    }
}
