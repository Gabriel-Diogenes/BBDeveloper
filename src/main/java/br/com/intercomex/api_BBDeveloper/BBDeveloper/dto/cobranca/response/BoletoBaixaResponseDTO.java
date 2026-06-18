package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BoletoBaixaResponseDTO(
        String numeroContratoCobranca,
        String dataBaixa,
        String horarioBaixa,
        Integer codigoErroRegistro,
        String mensagem
) {
}
