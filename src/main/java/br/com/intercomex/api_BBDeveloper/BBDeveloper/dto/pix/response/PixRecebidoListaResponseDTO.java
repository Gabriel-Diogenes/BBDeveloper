package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PixRecebidoListaResponseDTO(
        Object parametros,
        List<PixRecebidoDTO> pix
) {
}
