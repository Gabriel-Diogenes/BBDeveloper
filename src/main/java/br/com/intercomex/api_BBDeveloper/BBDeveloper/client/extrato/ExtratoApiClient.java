package br.com.intercomex.api_BBDeveloper.BBDeveloper.client.extrato;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.support.BBClientSupport;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.extrato.response.ExtratoResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class ExtratoApiClient extends BBClientSupport {

    private static final String API = "Extrato";

    public ExtratoApiClient(BBApiProperties properties, WebClient bbWebClient) {
        super(properties, bbWebClient);
    }

    public ExtratoResponseDTO consultar(
            String token,
            String agencia,
            String conta,
            String dataInicio,
            String dataFim,
            Integer pagina,
            Integer quantidadePorPagina) {
        log.info("Consultando extrato — agência: {}, conta: {}, período: {} a {}", agencia, conta, dataInicio, dataFim);

        int paginaEfetiva = pagina != null ? pagina : 1;
        int quantidadeEfetiva = quantidadePorPagina != null ? quantidadePorPagina : 200;

        String uri = UriComponentsBuilder
                .fromUriString(extratoUri("/conta-corrente/agencia/" + agencia + "/conta/" + conta))
                .queryParam("gw-dev-app-key", properties.getDeveloperKey())
                .queryParam("dataInicioSolicitacao", dataInicio)
                .queryParam("dataFimSolicitacao", dataFim)
                .queryParam("numeroPaginaSolicitacao", paginaEfetiva)
                .queryParam("quantidadeRegistroPaginaSolicitacao", quantidadeEfetiva)
                .build()
                .toUriString();

        log.info("URL extrato BB: {}", uri);

        var request = bbWebClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .header("gw-dev-app-key", properties.getDeveloperKey())
                .accept(MediaType.APPLICATION_JSON);

        if (properties.getExtratoHomologacaoHeader() != null && !properties.getExtratoHomologacaoHeader().isBlank()) {
            request = request.header("x-br-com-bb-ipa-mciteste", properties.getExtratoHomologacaoHeader());
        }

        return request.retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "consultar extrato"))
                .bodyToMono(ExtratoResponseDTO.class)
                .doOnSuccess(r -> log.info("Extrato consultado — {} lançamentos na página",
                        r.quantidadeRegistroPaginaAtual()))
                .block();
    }
}
