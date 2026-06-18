package br.com.intercomex.api_BBDeveloper.BBDeveloper.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtratoUtilTest {

    @Test
    void formatarAgencia_removeZerosAEsquerda() {
        assertEquals("452", ExtratoUtil.formatarAgencia("0452"));
    }

    @Test
    void formatarAgencia_rejeitaVazio() {
        assertThrows(IllegalArgumentException.class, () -> ExtratoUtil.formatarAgencia("  "));
    }

    @Test
    void formatarConta_removeDigitosNaoNumericos() {
        assertEquals("123873", ExtratoUtil.formatarConta("123.873"));
    }

    @ParameterizedTest
    @CsvSource({
            "19.04.2023, 19042023",
            "19/04/2023, 19042023",
            "2023-04-19, 19042023",
            "19042023, 19042023"
    })
    void formatarDataSolicitacao_converteFormatosComuns(String entrada, String esperado) {
        assertEquals(esperado, ExtratoUtil.formatarDataSolicitacao(entrada));
    }

    @Test
    void formatarDataSolicitacao_rejeitaFormatoInvalido() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ExtratoUtil.formatarDataSolicitacao("data-invalida"));

        assertTrue(ex.getMessage().contains("Data inválida"));
    }
}
