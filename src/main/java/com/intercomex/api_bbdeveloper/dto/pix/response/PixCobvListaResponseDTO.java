package com.intercomex.api_bbdeveloper.dto.pix.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PixCobvListaResponseDTO(
        Object parametros,
        @JsonProperty("cobs") List<PixCobvResponseDTO> cobsv
) {
}
