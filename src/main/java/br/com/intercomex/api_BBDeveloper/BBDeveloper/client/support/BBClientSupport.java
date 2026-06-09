package br.com.intercomex.api_BBDeveloper.BBDeveloper.client.support;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public abstract class BBClientSupport {

    protected final BBApiProperties properties;
    protected final WebClient bbWebClient;

    protected String pixUri(String path) {
        return properties.getPixBaseUrl() + path + "?gw-dev-app-key=" + properties.getDeveloperKey();
    }

    protected String extratoUri(String path) {
        return properties.getExtratoBaseUrl() + path;
    }

    protected String cobrancaUri(String path) {
        return properties.getCobrancaBaseUrl() + path + "?gw-dev-app-key=" + properties.getDeveloperKey();
    }

    protected Function<ClientResponse, Mono<? extends Throwable>> errorHandler(String api, String operacao) {
        return response -> response.bodyToMono(String.class).flatMap(body -> {
            log.error("Erro {} BB ({}) — Status: {} Body: {}", api, operacao, response.statusCode(), body);
            return Mono.error(new RuntimeException("Erro BB API " + api + ": " + body));
        });
    }

    protected Function<ClientResponse, Mono<? extends Throwable>> oauthErrorHandler() {
        return response -> response.bodyToMono(String.class).flatMap(body -> {
            log.error("Erro OAuth BB — Status: {} Body: {}", response.statusCode(), body);
            return Mono.error(new RuntimeException("Erro BB OAuth: " + body));
        });
    }

}
