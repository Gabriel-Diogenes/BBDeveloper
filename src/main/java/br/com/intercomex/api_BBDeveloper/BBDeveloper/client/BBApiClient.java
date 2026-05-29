package br.com.intercomex.api_BBDeveloper.BBDeveloper.client;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.auth.TokenResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobrancaImediataDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobrancaRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
        log.info("Criando cobrança Pix no BB Developer — txid: {}", txid);
        log.debug("Payload: {}", request);

        return bbWebClient
                .put()
                .uri(properties.getPixBaseUrl() + "/cob/" + txid 
                        + "?gw-dev-app-key=" + properties.getDeveloperKey())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> 
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Erro na API BB - Status: {} - Body: {}", 
                                            response.statusCode(), body);
                                    return Mono.error(new RuntimeException("Erro BB API: " + body));
                                }))
                .bodyToMono(PixCobrancaImediataDTO.class)
                .doOnSuccess(response -> log.info("Cobrança criada com sucesso: txid={}, status={}", 
                        response.getTxid(), response.getStatus()))
                .doOnError(error -> log.error("Erro ao criar cobrança: {}", error.getMessage()))
                .block();
    }
}
