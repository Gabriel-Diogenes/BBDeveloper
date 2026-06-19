package com.intercomex.api_bbdeveloper.dto.pix.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PixRecebidoDTO(
        String endToEndId,
        String txid,
        String valor,
        Object componentesValor,
        String chave,
        String horario,
        String infoPagador,
        List<PixDevolucaoDTO> devolucoes
) {
}
