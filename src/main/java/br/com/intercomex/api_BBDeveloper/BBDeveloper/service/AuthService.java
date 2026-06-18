package br.com.intercomex.api_BBDeveloper.BBDeveloper.service;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.auth.BBOAuthClient;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.auth.TokenResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MARGEM_SEGURANCA_SEGUNDOS = 60;
    private static final int TTL_MINIMO_SEGUNDOS = 30;

    private final BBOAuthClient oauthClient;
    private final BBApiProperties properties;
    private final Object cacheLock = new Object();
    private volatile CachedToken cachedToken;

    public TokenResponseDTO gerarToken() {
        CachedToken atual = cachedToken;
        if (atual != null && Instant.now().isBefore(atual.expiraEm())) {
            log.debug("Token OAuth reutilizado do cache (expira em {})", atual.expiraEm());
            return atual.token();
        }

        synchronized (cacheLock) {
            atual = cachedToken;
            if (atual != null && Instant.now().isBefore(atual.expiraEm())) {
                log.debug("Token OAuth reutilizado do cache após lock");
                return atual.token();
            }

            log.debug("Iniciando geração de token — scope: {}", properties.getScope());
            TokenResponseDTO token = oauthClient.obterToken();

            if (token == null || token.accessToken() == null) {
                log.error("Falha crítica: token retornou null ou sem access_token");
                throw new IllegalStateException("Falha ao obter token de acesso");
            }

            int ttl = token.expiresIn() != null ? token.expiresIn() : 600;
            int segundosValidos = Math.max(TTL_MINIMO_SEGUNDOS, ttl - MARGEM_SEGURANCA_SEGUNDOS);
            cachedToken = new CachedToken(token, Instant.now().plusSeconds(segundosValidos));

            log.info("Token obtido e armazenado em cache por ~{}s — scope solicitado: {}, scope concedido: {}",
                    segundosValidos, properties.getScope(), token.scope());
            return token;
        }
    }

    private record CachedToken(TokenResponseDTO token, Instant expiraEm) {
    }
}
