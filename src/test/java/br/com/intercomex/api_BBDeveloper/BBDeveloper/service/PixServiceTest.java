package br.com.intercomex.api_BBDeveloper.BBDeveloper.service;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.pix.PixApiClient;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobrancaRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixDevolucaoRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobrancaImediataDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixDevolucaoDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixRecebidoDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PixServiceTest {

    @Mock
    private PixApiClient pixApiClient;

    @Mock
    private AuthService authService;

    @InjectMocks
    private PixService pixService;

    private PixCobrancaRequestDTO cobRequest;

    @BeforeEach
    void configurar() {
        when(authService.gerarToken()).thenReturn(TestFixtures.token());
        cobRequest = new PixCobrancaRequestDTO(
                new PixCobrancaRequestDTO.Calendario(3600),
                new PixCobrancaRequestDTO.Devedor("12345678909", null, "Pagador Teste"),
                new PixCobrancaRequestDTO.Valor("10.00"),
                "9e881f18-cc66-4fc7-8f2c-a795dbb2bfc1",
                "Teste",
                null,
                null,
                null
        );
    }

    @Test
    void criarCob_delegaParaApiClientComTxidInformado() {
        PixCobrancaImediataDTO esperado = new PixCobrancaImediataDTO(
                TestFixtures.TXID_VALIDO, 0, null, "ATIVA", null, null, null, null, null, null);
        when(pixApiClient.criarCob(eq(TestFixtures.TXID_VALIDO), eq(cobRequest), eq(TestFixtures.ACCESS_TOKEN)))
                .thenReturn(esperado);

        PixCobrancaImediataDTO resultado = pixService.criarCob(TestFixtures.TXID_VALIDO, cobRequest);

        assertEquals(esperado, resultado);
    }

    @Test
    void criarCob_geraTxidQuandoNaoInformado() {
        ArgumentCaptor<String> txidCaptor = ArgumentCaptor.forClass(String.class);
        when(pixApiClient.criarCob(txidCaptor.capture(), eq(cobRequest), eq(TestFixtures.ACCESS_TOKEN)))
                .thenReturn(null);

        pixService.criarCob(null, cobRequest);

        String txidGerado = txidCaptor.getValue();
        verify(pixApiClient).criarCob(eq(txidGerado), eq(cobRequest), eq(TestFixtures.ACCESS_TOKEN));
    }

    @Test
    void consultarCob_rejeitaTxidInvalido() {
        assertThrows(IllegalArgumentException.class, () -> pixService.consultarCob("txid-curto"));
        verify(pixApiClient, never()).consultarCob(any(), any());
    }

    @Test
    void listarCobs_rejeitaPeriodoMaiorQueQuatroDias() {
        assertThrows(IllegalArgumentException.class, () -> pixService.listarCobs(
                "2026-06-01T00:00:00Z", "2026-06-10T00:00:00Z",
                null, null, null, null, null));
        verify(pixApiClient, never()).listarCobs(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void listarCobs_usaPeriodoInformado() {
        PixCobListaResponseDTO esperado = new PixCobListaResponseDTO(null, null);
        when(pixApiClient.listarCobs(
                eq(TestFixtures.ACCESS_TOKEN),
                eq(TestFixtures.PERIODO_INICIO),
                eq(TestFixtures.PERIODO_FIM),
                eq(null), eq(null), eq(null), eq(null), eq(null)))
                .thenReturn(esperado);

        PixCobListaResponseDTO resultado = pixService.listarCobs(
                TestFixtures.PERIODO_INICIO, TestFixtures.PERIODO_FIM,
                null, null, null, null, null);

        assertEquals(esperado, resultado);
    }

    @Test
    void consultarPixRecebido_validaE2eidAntesDeChamarApi() {
        PixRecebidoDTO esperado = new PixRecebidoDTO(
                TestFixtures.E2EID_VALIDO, null, "1.00", null, null, null, null, null);
        when(pixApiClient.consultarPixRecebido(TestFixtures.E2EID_VALIDO, TestFixtures.ACCESS_TOKEN))
                .thenReturn(esperado);

        PixRecebidoDTO resultado = pixService.consultarPixRecebido(TestFixtures.E2EID_VALIDO);

        assertEquals(esperado, resultado);
    }

    @Test
    void solicitarDevolucao_delegaParaApiClient() {
        PixDevolucaoRequestDTO request = new PixDevolucaoRequestDTO("0.01", null, null);
        PixDevolucaoDTO esperado = new PixDevolucaoDTO(
                TestFixtures.DEVOLUCAO_ID_VALIDO, null, "0.01", null, null, null, null, null);
        when(pixApiClient.solicitarDevolucao(
                TestFixtures.E2EID_VALIDO, TestFixtures.DEVOLUCAO_ID_VALIDO, request, TestFixtures.ACCESS_TOKEN))
                .thenReturn(esperado);

        PixDevolucaoDTO resultado = pixService.solicitarDevolucao(
                TestFixtures.E2EID_VALIDO, TestFixtures.DEVOLUCAO_ID_VALIDO, request);

        assertEquals(esperado, resultado);
    }
}
