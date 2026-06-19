package com.intercomex.api_bbdeveloper.service;

import com.intercomex.api_bbdeveloper.client.extrato.ExtratoApiClient;
import com.intercomex.api_bbdeveloper.dto.extrato.response.ExtratoResponseDTO;
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

    @InjectMocks
    private ExtratoService extratoService;

    @Test
    void consultarExtrato_formataParametrosEDelegaParaApiClient() {
        ExtratoResponseDTO esperado = new ExtratoResponseDTO(1, 30, null, null, 1, 30, null);
        when(extratoApiClient.consultar(
                "1505", "1348", "19042023", "23042023", 1, 120))
                .thenReturn(esperado);

        ExtratoResponseDTO resultado = extratoService.consultarExtrato(
                "1505", "1348", "19.04.2023", "23.04.2023", 1, 120);

        assertEquals(esperado, resultado);
        verify(extratoApiClient).consultar(
                "1505", "1348", "19042023", "23042023", 1, 120);
    }
}
