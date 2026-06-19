package com.intercomex.api_bbdeveloper.dto.pix.response;

import com.intercomex.api_bbdeveloper.dto.pix.request.PixCobrancaRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PixCobrancaImediataDTO(
        String txid,
        Integer revisao,
        String location,
        String status,
        Calendario calendario,
        PixCobrancaRequestDTO.Devedor devedor,
        PixCobrancaRequestDTO.Valor valor,
        String chave,
        String solicitacaoPagador,
        String pixCopiaECola
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Calendario(String criacao, Integer expiracao) {
    }
}
