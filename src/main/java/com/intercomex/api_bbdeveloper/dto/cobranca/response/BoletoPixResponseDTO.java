package com.intercomex.api_bbdeveloper.dto.cobranca.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BoletoPixResponseDTO(
        String id,
        @JsonProperty("txId")
        @JsonAlias("qrCode.txId")
        String txId,
        @JsonProperty("url")
        @JsonAlias("qrCode.url")
        String url,
        @JsonProperty("emv")
        @JsonAlias("qrCode.emv")
        String emv,
        @JsonAlias("qrCode.tipo")
        Integer qrCodeTipo,
        @JsonProperty("chave")
        @JsonAlias("pix.chave")
        String chave,
        @JsonProperty("valorRecebido")
        @JsonAlias("pix.valorRecebido")
        Double valorRecebido,
        @JsonAlias("pix.timestamp")
        String timestamp,
        @JsonAlias("pix.textoRetorno")
        String textoRetorno,
        String dataRegistroTituloCobranca,
        Integer agenciaBeneficiario,
        Integer contaBeneficiario,
        Double valorOriginalTituloCobranca,
        String validadeTituloCobranca
) {
}
