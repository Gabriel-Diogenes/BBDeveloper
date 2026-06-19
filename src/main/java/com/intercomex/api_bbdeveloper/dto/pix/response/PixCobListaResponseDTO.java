package com.intercomex.api_bbdeveloper.dto.pix.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PixCobListaResponseDTO(
        Object parametros,
        List<PixCobrancaImediataDTO> cobs
) {
}
