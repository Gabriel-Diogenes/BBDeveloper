package com.intercomex.api_bbdeveloper.controller;

import com.intercomex.api_bbdeveloper.dto.cobranca.request.BoletoRegistrarRequestDTO;
import com.intercomex.api_bbdeveloper.dto.cobranca.response.BoletoResponseDTO;
import com.intercomex.api_bbdeveloper.service.CobrancaService;
import com.intercomex.api_bbdeveloper.support.TestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CobrancaControllerTest {

    @Mock
    private CobrancaService cobrancaService;

    @InjectMocks
    private CobrancaController controller;

    @Test
    void registrarBoleto_retorna201Created() {
        BoletoRegistrarRequestDTO request = TestFixtures.boletoMinimo();
        BoletoResponseDTO resposta = new BoletoResponseDTO(
                "00031285570000000001", null, null, null, null, null, null, null, null, null, null, null);
        when(cobrancaService.registrarBoleto(request)).thenReturn(resposta);

        ResponseEntity<BoletoResponseDTO> response = controller.registrarBoleto(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(resposta, response.getBody());
    }
}
