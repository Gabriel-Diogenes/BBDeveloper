package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BoletoResponseDTO {

    private String numero;
    private String numeroCarteira;
    private String numeroVariacaoCarteira;
    private String boletoUrl;
    private String linhaDigitavel;
    private String codigoBarraNumerico;
    private String numeroContratoCobranca;
    private String dataVencimento;
    private Double valorOriginal;
    private String status;

    private PixQrCode qrCode;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PixQrCode {
        private String url;
        private String txId;
        private String emv;
    }

    private Beneficiario beneficiario;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Beneficiario {
        private String agencia;
        private String contaCorrente;
        private String tipoEndereco;
        private String logradouro;
        private String bairro;
        private String cidade;
        private String codigoCidade;
        private String uf;
        private String cep;
        private String indicadorComprovacao;
    }
}
