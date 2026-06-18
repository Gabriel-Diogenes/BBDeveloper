package br.com.intercomex.api_BBDeveloper.BBDeveloper.service;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.auth.BBOAuthClient;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.auth.TokenResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final BBOAuthClient oauthClient;
    private final BBApiProperties properties;

    public TokenResponseDTO gerarToken() {
        log.debug("Iniciando geração de token — scope: {}", properties.getScope());

        TokenResponseDTO token = oauthClient.obterToken();

        if (token == null || token.accessToken() == null) {
            log.error("Falha crítica: token retornou null ou sem access_token");
            throw new IllegalStateException("Falha ao obter token de acesso");
        }

        log.info("Token obtido — scope solicitado: {}, scope concedido: {}",
                properties.getScope(), token.scope());
        return token;
    }
}
