package br.com.intercomex.api_BBDeveloper.BBDeveloper.config;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Slf4j
public final class BBWebClientFilters {

    private BBWebClientFilters() {
    }

    public static ExchangeFilterFunction bearerAuthWithRetry(AuthService authService) {
        return (request, next) -> {
            if (usesBasicAuth(request)) {
                return next.exchange(request);
            }
            return exchangeWithBearer(request, next, authService, false);
        };
    }

    private static Mono<org.springframework.web.reactive.function.client.ClientResponse> exchangeWithBearer(
            ClientRequest request,
            ExchangeFunction next,
            AuthService authService,
            boolean retry) {
        ClientRequest authenticated = ClientRequest.from(request)
                .headers(headers -> headers.setBearerAuth(authService.getAccessToken()))
                .build();

        return next.exchange(authenticated).flatMap(response -> {
            if (response.statusCode().value() == 401 && !retry) {
                log.warn("Token OAuth rejeitado (401) em {} — invalidando cache e repetindo uma vez",
                        request.url());
                return response.releaseBody()
                        .then(Mono.fromRunnable(authService::invalidarToken))
                        .then(exchangeWithBearer(request, next, authService, true));
            }
            return Mono.just(response);
        });
    }

    private static boolean usesBasicAuth(ClientRequest request) {
        String authorization = request.headers().getFirst(HttpHeaders.AUTHORIZATION);
        return authorization != null && authorization.regionMatches(true, 0, "Basic ", 0, 6);
    }
}
