package br.com.intercomex.api_BBDeveloper.BBDeveloper.util;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.support.TestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PixUtilTest {

    @Test
    void gerarTxid_deveTer32CaracteresAlfanumericos() {
        String txid = PixUtil.gerarTxid();

        assertEquals(32, txid.length());
        assertTrue(txid.matches("[a-zA-Z0-9]+"));
    }

    @Test
    void validarTxid_aceitaFormatoValido() {
        assertDoesNotThrow(() -> PixUtil.validarTxid(TestFixtures.TXID_VALIDO));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "curto", "com-traço-invalido", "abcdefghijklmnopqrstuvwxy"})
    void validarTxid_rejeitaFormatoInvalido(String txid) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> PixUtil.validarTxid(txid.isEmpty() ? null : txid));

        assertTrue(ex.getMessage().contains("txid inválido"));
    }

    @Test
    void validarE2eid_aceitaFormatoValido() {
        assertDoesNotThrow(() -> PixUtil.validarE2eid(TestFixtures.E2EID_VALIDO));
    }

    @Test
    void validarE2eid_rejeitaSemPrefixoE() {
        assertThrows(IllegalArgumentException.class,
                () -> PixUtil.validarE2eid("0000000020260616131914574080657"));
    }

    @Test
    void validarDevolucaoId_aceitaFormatoValido() {
        assertDoesNotThrow(() -> PixUtil.validarDevolucaoId(TestFixtures.DEVOLUCAO_ID_VALIDO));
    }

    @Test
    void validarDevolucaoId_rejeitaVazio() {
        assertThrows(IllegalArgumentException.class, () -> PixUtil.validarDevolucaoId(""));
    }

    @Test
    void validarPeriodoListagem_aceitaIntervaloDeAteQuatroDias() {
        assertDoesNotThrow(() -> PixUtil.validarPeriodoListagem(
                TestFixtures.PERIODO_INICIO, TestFixtures.PERIODO_FIM));
    }

    @Test
    void validarPeriodoListagem_rejeitaIntervaloMaiorQueQuatroDias() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> PixUtil.validarPeriodoListagem(
                        "2026-06-01T00:00:00Z", "2026-06-06T00:00:00Z"));

        assertTrue(ex.getMessage().contains("menor que 5 dias"));
    }

    @Test
    void validarPeriodoListagem_rejeitaFimAntesDoInicio() {
        assertThrows(IllegalArgumentException.class,
                () -> PixUtil.validarPeriodoListagem(
                        "2026-06-10T00:00:00Z", "2026-06-01T00:00:00Z"));
    }

    @Test
    void periodoPadrao_retornaIntervaloValidoParaListagem() {
        String inicio = PixUtil.periodoInicioPadrao();
        String fim = PixUtil.periodoFimPadrao();

        assertDoesNotThrow(() -> PixUtil.validarPeriodoListagem(inicio, fim));
    }
}
