package com.intercomex.api_bbdeveloper.service;

import com.intercomex.api_bbdeveloper.client.cobranca.CobrancaApiClient;
import com.intercomex.api_bbdeveloper.dto.cobranca.request.BoletoRegistrarRequestDTO;
import com.intercomex.api_bbdeveloper.dto.cobranca.response.BoletoBaixaResponseDTO;
import com.intercomex.api_bbdeveloper.dto.cobranca.response.BoletoResponseDTO;
import com.intercomex.api_bbdeveloper.support.TestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CobrancaServiceTest {

    @Mock
    private CobrancaApiClient cobrancaApiClient;

    @InjectMocks
    private CobrancaService cobrancaService;

    @Test
    void registrarBoleto_rejeitaBodyNulo() {
        assertThrows(IllegalArgumentException.class, () -> cobrancaService.registrarBoleto(null));
        verify(cobrancaApiClient, never()).registrarBoleto(any());
    }

    @Test
    void registrarBoleto_rejeitaValorOriginalInvalido() {
        BoletoRegistrarRequestDTO request = new BoletoRegistrarRequestDTO(
                3128557, null, null, null, "18.06.2026", "18.07.2026", 0.0,
                null, null, null, null, null, null, null, null, null, null,
                TestFixtures.boletoMinimo().pagador());

        assertThrows(IllegalArgumentException.class, () -> cobrancaService.registrarBoleto(request));
    }

    @Test
    void registrarBoleto_aplicaDefaultsENormalizaPagador() {
        BoletoRegistrarRequestDTO request = TestFixtures.boletoMinimo();
        BoletoResponseDTO resposta = new BoletoResponseDTO(
                "00031285570000000001", null, null, null, null, null, null, null, null, null, null, null);
        ArgumentCaptor<BoletoRegistrarRequestDTO> captor = ArgumentCaptor.forClass(BoletoRegistrarRequestDTO.class);
        when(cobrancaApiClient.registrarBoleto(captor.capture()))
                .thenReturn(resposta);

        BoletoResponseDTO resultado = cobrancaService.registrarBoleto(request);

        assertEquals(resposta, resultado);
        BoletoRegistrarRequestDTO enviado = captor.getValue();
        assertEquals(17, enviado.numeroCarteira());
        assertEquals(35, enviado.numeroVariacaoCarteira());
        assertEquals("S", enviado.indicadorPix());
        assertEquals("12345678909", enviado.pagador().numeroInscricao());
        assertEquals(1, enviado.pagador().tipoInscricao());
        assertEquals("SERVICO PRESTADO", enviado.campoUtilizacaoBeneficiario());
    }

    @Test
    void registrarBoleto_truncaCampoUtilizacaoBeneficiario() {
        BoletoRegistrarRequestDTO request = new BoletoRegistrarRequestDTO(
                3128557, 17, 35, 1, "18.06.2026", "18.07.2026", 10.0,
                "N", "A", 2, "DUPLICATA MERCANTIL", "N", "1",
                "Texto muito longo para o campo de utilizacao do beneficiario",
                "00031285570000000001", "", "S",
                TestFixtures.boletoMinimo().pagador());
        ArgumentCaptor<BoletoRegistrarRequestDTO> captor = ArgumentCaptor.forClass(BoletoRegistrarRequestDTO.class);
        when(cobrancaApiClient.registrarBoleto(captor.capture()))
                .thenReturn(null);

        cobrancaService.registrarBoleto(request);

        assertTrue(captor.getValue().campoUtilizacaoBeneficiario().length() <= 25);
    }

    @Test
    void registrarBoletoSimplificado_montaPayloadComPix() {
        ArgumentCaptor<BoletoRegistrarRequestDTO> captor = ArgumentCaptor.forClass(BoletoRegistrarRequestDTO.class);
        when(cobrancaApiClient.registrarBoleto(captor.capture()))
                .thenReturn(null);

        cobrancaService.registrarBoletoSimplificado(
                3128557, "Francisco da Silva", "12345678909", 10.0, 30, true);

        BoletoRegistrarRequestDTO enviado = captor.getValue();
        assertEquals("S", enviado.indicadorPix());
        assertEquals(10.0, enviado.valorOriginal());
        assertEquals("Francisco da Silva", enviado.pagador().nome());
    }

    @Test
    void cancelarBoleto_usaEndpointBaixarDoBb() {
        BoletoBaixaResponseDTO resposta = new BoletoBaixaResponseDTO("0001", "18.06.2026", "10:00:00", null, null);
        when(cobrancaApiClient.baixarBoleto("00031285570000000001", 3128557))
                .thenReturn(resposta);

        BoletoBaixaResponseDTO resultado = cobrancaService.cancelarBoleto("00031285570000000001", 3128557);

        assertEquals(resposta, resultado);
        verify(cobrancaApiClient).baixarBoleto("00031285570000000001", 3128557);
    }

    @Test
    void listarBoletos_formataAgenciaEConta() {
        ArgumentCaptor<String> agenciaCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contaCaptor = ArgumentCaptor.forClass(String.class);
        when(cobrancaApiClient.listarBoletos(
                eq(3128557),
                agenciaCaptor.capture(), contaCaptor.capture(), any(), any()))
                .thenReturn(null);

        cobrancaService.listarBoletos(3128557, "0452", "123.873");

        assertEquals("452", agenciaCaptor.getValue());
        assertEquals("123873", contaCaptor.getValue());
    }
}
