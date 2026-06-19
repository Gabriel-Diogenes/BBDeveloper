package com.intercomex.api_bbdeveloper.dto.pix.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PixDevolucaoDTO(
        String id,
        String rtrId,
        String valor,
        String natureza,
        String descricao,
        Object horario,
        String status,
        String motivo
) {
}
