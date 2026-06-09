package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BoletoPixResponseDTO {

    private String txId;
    private String status;
    private String emv;
    private String url;
    private String chave;
    private String valor;
    private String expiracao;
}
