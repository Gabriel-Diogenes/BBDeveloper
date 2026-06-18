package br.com.intercomex.api_BBDeveloper.BBDeveloper.support;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.auth.TokenResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.request.BoletoRegistrarRequestDTO;

public final class TestFixtures {

    public static final String TXID_VALIDO = "abcdefghijklmnopqrstuvwxyz12";
    public static final String E2EID_VALIDO = "E0000000020260616131914574080657";
    public static final String DEVOLUCAO_ID_VALIDO = "devolucaoId123456789";
    public static final String PERIODO_INICIO = "2026-06-01T00:00:00Z";
    public static final String PERIODO_FIM = "2026-06-04T23:59:59Z";
    public static final String ACCESS_TOKEN = "token-teste-abc";

    private TestFixtures() {
    }

    public static TokenResponseDTO token(int expiresIn) {
        return new TokenResponseDTO(ACCESS_TOKEN, "Bearer", expiresIn, "pix.read");
    }

    public static TokenResponseDTO token() {
        return token(600);
    }

    public static BoletoRegistrarRequestDTO boletoMinimo() {
        return new BoletoRegistrarRequestDTO(
                3128557,
                null,
                null,
                null,
                "18.06.2026",
                "18.07.2026",
                10.0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "00031285570000000001",
                null,
                null,
                new BoletoRegistrarRequestDTO.Pagador(
                        null,
                        "123.456.789-09",
                        "Francisco da Silva",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );
    }
}
