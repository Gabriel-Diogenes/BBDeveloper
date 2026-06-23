package com.intercomex.api_bbdeveloper.exception.bb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BBErrorResolver {

    public static final String ERRO_DESCONHECIDO =
            "Erro desconhecido retornado pelo Banco do Brasil. Consulte o suporte técnico informando o código e a operação realizada.";

    private final BBErrorCatalog catalog;
    private final BBErrorResponseParser parser;

    public ResolvedBBError resolve(String api, String responseBody) {
        return parser.extractCodigo(responseBody)
                .flatMap(codigo -> catalog.find(api, codigo).map(entry -> new ResolvedBBError(entry.codigo(), entry.descricao())))
                .orElseGet(() -> new ResolvedBBError(
                        parser.extractCodigo(responseBody).orElse("DESCONHECIDO"),
                        ERRO_DESCONHECIDO));
    }

    public record ResolvedBBError(String codigo, String descricao) {
    }
}
