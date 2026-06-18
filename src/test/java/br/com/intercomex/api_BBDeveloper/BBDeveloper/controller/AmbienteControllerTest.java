package br.com.intercomex.api_BBDeveloper.BBDeveloper.controller;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.config.AmbienteResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmbienteControllerTest {

    @Mock
    private BBApiProperties properties;

    @InjectMocks
    private AmbienteController controller;

    @Test
    void consultarAmbiente_retornaConfiguracaoAtiva() {
        when(properties.getAmbiente()).thenReturn("homologacao");
        when(properties.getOauthUrl()).thenReturn("https://oauth.test/token");
        when(properties.getPixBaseUrl()).thenReturn("https://pix.test/v2");
        when(properties.getCobrancaBaseUrl()).thenReturn("https://cobranca.test/v2");
        when(properties.getExtratoBaseUrl()).thenReturn("https://extrato.test/v2");
        when(properties.getExtratoHomologacaoHeader()).thenReturn("178961031");
        when(properties.isPixRequerMtls()).thenReturn(false);

        ResponseEntity<AmbienteResponseDTO> response = controller.consultarAmbiente();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        AmbienteResponseDTO body = response.getBody();
        assertEquals("homologacao", body.ambiente());
        assertEquals("https://oauth.test/token", body.oauthUrl());
        assertTrue(body.extratoHeaderHomologacaoAtivo());
        assertEquals(false, body.pixRequerMtls());
    }
}
