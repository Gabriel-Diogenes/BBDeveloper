package br.com.intercomex.api_BBDeveloper.BBDeveloper.service;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.PixCobrancaRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.PixCobrancaImediata;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.TokenResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PixService {

    private final BBApiProperties properties;
    private final WebClient bbWebClient;
    private final AuthService authService;

    public PixCobrancaImediata criarCobranca() {

        TokenResponseDTO token = authService.gerarToken();

        if (token == null || token.getAccess_token() == null) {
            throw new IllegalStateException("Falha ao obter token de acesso");
        }

        String txid = UUID.randomUUID()
                .toString()
                .replace("-", "");

        PixCobrancaRequestDTO request = PixCobrancaRequestDTO.builder()
                .calendario(
                        PixCobrancaRequestDTO.Calendario.builder()
                                .expiracao(3600)
                                .build()
                )
                .devedor(
                        PixCobrancaRequestDTO.Devedor.builder()
                                .cpf("12345678910")
                                .nome("Empresa de Serviços SA")
                                .build()
                )
                .valor(
                        PixCobrancaRequestDTO.Valor.builder()
                                .original("1.00")
                                .build()
                )


                .chave(properties.getPixKey())

                .solicitacaoPagador("Pagamento de teste Pix")
                .build();

        log.info("Criando cobrança Pix — txid: {}", txid);
        log.info("Payload enviado ao BB: {}", request);

        return bbWebClient
                .put()
                .uri(
                        properties.getPixBaseUrl()
                                + "/cob/"
                                + txid
                                + "?gw-dev-app-key="
                                + properties.getDeveloperKey()
                )
                .header("Authorization", "Bearer " + token.getAccess_token())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)

                .retrieve()

                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {

                                    log.error("Erro retornado pelo BB:");
                                    log.error("Status: {}", response.statusCode());
                                    log.error("Body: {}", body);

                                    return Mono.error(
                                            new RuntimeException(
                                                    "Erro BB API: " + body
                                            )
                                    );
                                })
                )

                .bodyToMono(PixCobrancaImediata.class)

                .doOnSuccess(response ->
                        log.info("Cobrança criada com sucesso: {}", response)
                )

                .block();
    }
}
