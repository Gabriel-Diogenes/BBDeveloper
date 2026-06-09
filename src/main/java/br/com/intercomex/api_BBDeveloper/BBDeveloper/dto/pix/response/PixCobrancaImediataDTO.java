package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobrancaRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PixCobrancaImediataDTO {

    private String txid;
    private Integer revisao;
    private String location;
    private String status;
    private Calendario calendario;
    private PixCobrancaRequestDTO.Devedor devedor;
    private PixCobrancaRequestDTO.Valor valor;
    private String chave;
    private String solicitacaoPagador;
    private String pixCopiaECola;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Calendario {
        private String criacao;
        private Integer expiracao;
    }
}
