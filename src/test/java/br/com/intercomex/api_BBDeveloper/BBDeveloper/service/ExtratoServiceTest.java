package br.com.intercomex.api_BBDeveloper.BBDeveloper.service;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.extrato.ExtratoApiClient;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.extrato.response.ExtratoResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExtratoServiceTest {

    @Mock
    private ExtratoApiClient extratoApiClient;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ExtratoService extratoService;

    @BeforeEach
    void configurarToken() {
        when(authService.gerarToken()).thenReturn(TestFixtures.token());
    }

    @Test
    void consultarExtrato_formataParametrosEDelegaParaApiClient() {
        ExtratoResponseDTO esperado = new ExtratoResponseDTO(1, 30, null, null, 1, 30, null);
        when(extratoApiClient.consultar(
                TestFixtures.ACCESS_TOKEN, "1505", "1348", "19042023", "23042023", 1, 120))
                .thenReturn(esperado);

        ExtratoResponseDTO resultado = extratoService.consultarExtrato(
                "1505", "1348", "19.04.2023", "23.04.2023", 1, 120);

        assertEquals(esperado, resultado);
        verify(extratoApiClient).consultar(
                TestFixtures.ACCESS_TOKEN, "1505", "1348", "19042023", "23042023", 1, 120);
    }
}
