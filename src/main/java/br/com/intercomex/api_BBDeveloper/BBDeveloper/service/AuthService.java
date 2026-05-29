package br.com.intercomex.api_BBDeveloper.BBDeveloper.service;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.BBApiClient;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.auth.TokenResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final BBApiClient bbApiClient;

    public TokenResponseDTO gerarToken() {
        log.debug("Iniciando geração de token de autenticação");
        
        TokenResponseDTO token = bbApiClient.obterToken();
        
        if (token == null || token.getAccess_token() == null) {
            log.error("Falha crítica: token retornou null ou sem access_token");
            throw new IllegalStateException("Falha ao obter token de acesso");
        }
        
        return token;
    }
}
