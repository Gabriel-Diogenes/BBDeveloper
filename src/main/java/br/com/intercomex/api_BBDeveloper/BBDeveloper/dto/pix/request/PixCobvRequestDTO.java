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
public class PixCobvRequestDTO {

    private Calendario calendario;
    private Devedor devedor;
    private Valor valor;
    private String chave;
    private String solicitacaoPagador;
    private List<InfoAdicional> infoAdicionais;
    private Loc loc;
    private String status;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Calendario {
        private String dataDeVencimento;
        private Integer validadeAposVencimento;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Devedor {
        private String cpf;
        private String cnpj;
        private String nome;
        private String email;
        private String logradouro;
        private String cidade;
        private String uf;
        private String cep;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Valor {
        private String original;
        private Multa multa;
        private Juros juros;
        private Desconto desconto;
        private Abatimento abatimento;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Multa {
        private Integer modalidade;
        private String valorPerc;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Juros {
        private Integer modalidade;
        private String valorPerc;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Desconto {
        private Integer modalidade;
        private List<DescontoDataFixa> descontoDataFixa;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DescontoDataFixa {
        private String data;
        private String valorPerc;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Abatimento {
        private Integer modalidade;
        private String valorPerc;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InfoAdicional {
        private String nome;
        private String valor;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Loc {
        private Long id;
    }
}
