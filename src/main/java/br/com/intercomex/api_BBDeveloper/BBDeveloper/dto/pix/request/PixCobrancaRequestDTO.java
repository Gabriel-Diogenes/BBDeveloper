package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor          
@AllArgsConstructor         
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PixCobrancaRequestDTO {

    private Calendario calendario;
    private Devedor devedor;
    private Valor valor;
    private String chave;
    private String solicitacaoPagador;
    private List<InfoAdicional> infoAdicionais;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Calendario {
        private Integer expiracao;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Devedor {
        private String cpf;
        private String cnpj;
        private String nome;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Valor {
        private String original;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InfoAdicional {
        private String nome;
        private String valor;
    }
}