package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BoletoListaResponseDTO(
        Integer indicadorContinuidade,
        Integer quantidadeItensPorPagina,
        Integer quantidadeTotalItens,
        List<BoletoResponseDTO> boletos
) {
}
