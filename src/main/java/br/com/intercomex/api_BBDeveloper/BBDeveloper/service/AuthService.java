package br.com.intercomex.api_BBDeveloper.BBDeveloper.service;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.TokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final BBApiProperties properties;

    private final WebClient bbWebClient;

    public TokenResponseDTO gerarToken() {

        String credentials =
                properties.getClientId() + ":" + properties.getClientSecret();

        String base64Credentials =
                Base64.getEncoder()
                        .encodeToString(
                                credentials.getBytes(StandardCharsets.UTF_8)
                        );

        return bbWebClient
                .post()
                .uri(properties.getOauthUrl())

                .header(
                        "Authorization",
                        "Basic " + base64Credentials
                )

                .header(
                        "gw-dev-app-key",
                        properties.getDeveloperKey()
                )

                .contentType(
                        MediaType.APPLICATION_FORM_URLENCODED
                )

                .body(
                        BodyInserters
                                .fromFormData(
                                        "grant_type",
                                        "client_credentials"
                                )
                                .with(
                                        "scope",
                                        properties.getScope()
                                )
                )

                .retrieve()

                .bodyToMono(TokenResponseDTO.class)

                .block();
    }
}
