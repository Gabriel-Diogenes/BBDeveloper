package br.com.intercomex.api_BBDeveloper.BBDeveloper.client.auth;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.support.BBClientSupport;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.auth.TokenResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class BBOAuthClient extends BBClientSupport {

    public BBOAuthClient(BBApiProperties properties, @Qualifier("bbWebClient") WebClient bbWebClient) {
        super(properties, bbWebClient);
    }

    public TokenResponseDTO obterToken() {
        return obterToken(properties.getScope());
    }

    public TokenResponseDTO obterToken(String scope) {
        log.debug("Solicitando token OAuth2 — scope: {}", scope);

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
                        .with("scope", scope))
                .retrieve()
                .onStatus(HttpStatusCode::isError, oauthErrorHandler())
                .bodyToMono(TokenResponseDTO.class)
                .doOnSuccess(token -> log.info("Token obtido — válido por {} segundos", token.expiresIn()))
                .doOnError(error -> log.error("Erro ao obter token: {}", error.getMessage()))
                .block();
    }
}
