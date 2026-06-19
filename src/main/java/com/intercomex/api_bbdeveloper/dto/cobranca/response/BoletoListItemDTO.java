package com.intercomex.api_bbdeveloper.dto.cobranca.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BoletoListItemDTO(
        String numeroBoletoBB,
        String dataRegistro,
        String dataVencimento,
        Double valorOriginal,
        Integer carteiraConvenio,
        Integer variacaoCarteiraConvenio,
        Integer codigoEstadoTituloCobranca,
        String estadoTituloCobranca,
        Integer contrato,
        String dataMovimento,
        String dataCredito,
        Double valorAtual,
        Double valorPago
) {
}
