package br.com.intercomex.api_BBDeveloper.BBDeveloper.client.extrato;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.support.BBClientSupport;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.extrato.response.ExtratoResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class ExtratoApiClient extends BBClientSupport {

    private static final String API = "Extrato";
    private static final int QUANTIDADE_POR_PAGINA_MINIMA = 30;
    private static final int QUANTIDADE_POR_PAGINA_MAXIMA = 120;

    public ExtratoApiClient(BBApiProperties properties, @Qualifier("bbMtlsWebClient") WebClient bbWebClient) {
        super(properties, bbWebClient);
    }

    public ExtratoResponseDTO consultar(
            String agencia,
            String conta,
            String dataInicio,
            String dataFim,
            Integer pagina,
            Integer quantidadePorPagina) {
        log.info("Consultando extrato — agência: {}, conta: {}, período: {} a {}", agencia, conta, dataInicio, dataFim);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(extratoUri("/conta-corrente/agencia/" + agencia + "/conta/" + conta))
                .queryParam("gw-dev-app-key", properties.getDeveloperKey())
                .queryParam("dataInicioSolicitacao", dataInicio)
                .queryParam("dataFimSolicitacao", dataFim);

        if (pagina != null) {
            uriBuilder.queryParam("numeroPaginaSolicitacao", pagina);
        }
        if (quantidadePorPagina != null) {
            uriBuilder.queryParam("quantidadeRegistroPaginaSolicitacao", normalizarQuantidadePorPagina(quantidadePorPagina));
        }

        String uri = uriBuilder.build().toUriString();

        log.info("URL extrato BB: {}", uri);

        var request = bbWebClient.get()
                .uri(uri)
                .header("gw-dev-app-key", properties.getDeveloperKey())
                .accept(MediaType.APPLICATION_JSON);

        String mciHeader = properties.getExtratoHomologacaoHeader();
        if (mciHeader != null && !mciHeader.isBlank()) {
            request = request.header("x-br-com-bb-ipa-mciteste", mciHeader);
        }

        log.info("Headers extrato — x-br-com-bb-ipa-mciteste: {}, gw-dev-app-key: {}",
                mciHeader, properties.getDeveloperKey());

        return request.retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler(API, "consultar extrato"))
                .bodyToMono(ExtratoResponseDTO.class)
                .doOnSuccess(r -> log.info("Extrato consultado — {} lançamentos na página",
                        r.quantidadeRegistroPaginaAtual()))
                .block();
    }

    private int normalizarQuantidadePorPagina(int quantidade) {
        if (quantidade < QUANTIDADE_POR_PAGINA_MINIMA) {
            log.warn("quantidadePorPagina {} abaixo do mínimo BB ({}); usando {}",
                    quantidade, QUANTIDADE_POR_PAGINA_MINIMA, QUANTIDADE_POR_PAGINA_MINIMA);
            return QUANTIDADE_POR_PAGINA_MINIMA;
        }
        if (quantidade > QUANTIDADE_POR_PAGINA_MAXIMA) {
            log.warn("quantidadePorPagina {} excede o máximo BB ({}); usando {}",
                    quantidade, QUANTIDADE_POR_PAGINA_MAXIMA, QUANTIDADE_POR_PAGINA_MAXIMA);
            return QUANTIDADE_POR_PAGINA_MAXIMA;
        }
        return quantidade;
    }
}
