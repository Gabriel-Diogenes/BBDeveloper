package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BoletoPixResponseDTO(
        String txId,
        String status,
        String emv,
        String url,
        String chave,
        String valor,
        String expiracao
) {
}
