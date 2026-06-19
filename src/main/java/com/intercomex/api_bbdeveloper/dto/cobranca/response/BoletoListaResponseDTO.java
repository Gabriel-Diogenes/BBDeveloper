package com.intercomex.api_bbdeveloper.dto.cobranca.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BoletoListaResponseDTO(
        String indicadorContinuidade,
        Integer quantidadeItensPorPagina,
        Integer quantidadeTotalItens,
        List<BoletoListItemDTO> boletos
) {
}
