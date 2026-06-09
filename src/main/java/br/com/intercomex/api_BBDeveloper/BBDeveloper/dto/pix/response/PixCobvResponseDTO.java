package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobvRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PixCobvResponseDTO {

    private String txid;
    private Integer revisao;
    private String location;
    private String status;
    private Calendario calendario;
    private PixCobvRequestDTO.Devedor devedor;
    private PixCobvRequestDTO.Valor valor;
    private String chave;
    private String solicitacaoPagador;
    private String pixCopiaECola;
    private Recebedor recebedor;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Calendario {
        private String criacao;
        private String dataDeVencimento;
        private Integer validadeAposVencimento;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Recebedor {
        private String cpf;
        private String cnpj;
        private String nome;
        private String logradouro;
        private String cidade;
        private String uf;
        private String cep;
    }
}
