package br.com.intercomex.api_BBDeveloper.BBDeveloper.service;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.auth.BBOAuthClient;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.auth.TokenResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private BBOAuthClient oauthClient;

    @Mock
    private BBApiProperties properties;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void configurarProperties() {
        when(properties.getScope()).thenReturn("pix.read");
    }

    @Test
    void gerarToken_reutilizaCacheEmChamadasSeguidas() {
        TokenResponseDTO token = TestFixtures.token(600);
        when(oauthClient.obterToken()).thenReturn(token);

        TokenResponseDTO primeira = authService.gerarToken();
        TokenResponseDTO segunda = authService.gerarToken();

        assertSame(primeira, segunda);
        verify(oauthClient, times(1)).obterToken();
    }

    @Test
    void gerarToken_retornaTokenDoOAuthClient() {
        TokenResponseDTO token = TestFixtures.token(600);
        when(oauthClient.obterToken()).thenReturn(token);

        TokenResponseDTO resultado = authService.gerarToken();

        assertEquals(TestFixtures.ACCESS_TOKEN, resultado.accessToken());
    }

    @Test
    void gerarToken_lancaExcecaoQuandoAccessTokenAusente() {
        when(oauthClient.obterToken()).thenReturn(new TokenResponseDTO(null, "Bearer", 600, "pix.read"));

        assertThrows(IllegalStateException.class, () -> authService.gerarToken());
    }

    @Test
    void gerarToken_lancaExcecaoQuandoRespostaNula() {
        when(oauthClient.obterToken()).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> authService.gerarToken());
    }

    @Test
    void getAccessToken_retornaAccessTokenDoCache() {
        when(oauthClient.obterToken()).thenReturn(TestFixtures.token(600));

        assertEquals(TestFixtures.ACCESS_TOKEN, authService.getAccessToken());
    }

    @Test
    void invalidarToken_forcaNovaChamadaAoOAuth() {
        when(oauthClient.obterToken()).thenReturn(TestFixtures.token(600));

        authService.gerarToken();
        authService.invalidarToken();
        authService.gerarToken();

        verify(oauthClient, times(2)).obterToken();
    }
}
