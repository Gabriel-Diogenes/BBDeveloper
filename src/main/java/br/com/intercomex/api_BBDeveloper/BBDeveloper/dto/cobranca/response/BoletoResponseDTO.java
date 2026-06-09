package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BoletoResponseDTO(
        String numero,
        String numeroCarteira,
        String numeroVariacaoCarteira,
        String boletoUrl,
        String linhaDigitavel,
        String codigoBarraNumerico,
        String numeroContratoCobranca,
        String dataVencimento,
        Double valorOriginal,
        String status,
        PixQrCode qrCode,
        Beneficiario beneficiario
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PixQrCode(String url, String txId, String emv) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Beneficiario(
            String agencia,
            String contaCorrente,
            String tipoEndereco,
            String logradouro,
            String bairro,
            String cidade,
            String codigoCidade,
            String uf,
            String cep,
            String indicadorComprovacao
    ) {
    }
}
