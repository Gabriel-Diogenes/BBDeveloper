package com.intercomex.api_bbdeveloper.dto.pix.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PixRecebidoListaResponseDTO(
        Object parametros,
        List<PixRecebidoDTO> pix
) {
}
