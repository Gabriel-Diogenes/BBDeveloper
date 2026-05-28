package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto;

import lombok.Data;

@Data
public class PixConsultaResponseDTO {

    private String txid;
    private String status;
    private String chave;
    private String solicitacaoPagador;
    private String location;
    private PixValor valor;
    private PixCalendario calendario;
    private PixDevedor devedor;

    @Data
    public static class PixValor {
        private String original;
    }

    @Data
    public static class PixCalendario {
        private String criacao;
        private int expiracao;
    }

    @Data
    public static class PixDevedor {
        private String cpf;
        private String nome;
    }
}
