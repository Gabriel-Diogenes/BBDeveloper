package com.intercomex.api_bbdeveloper.dto.pix.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PixCobvRequestDTO(
        Calendario calendario,
        Devedor devedor,
        Valor valor,
        String chave,
        String solicitacaoPagador,
        List<InfoAdicional> infoAdicionais,
        Loc loc,
        String status
) {
    public record Calendario(String dataDeVencimento, Integer validadeAposVencimento) {
    }

    public record Devedor(
            String cpf,
            String cnpj,
            String nome,
            String email,
            String logradouro,
            String cidade,
            String uf,
            String cep
    ) {
    }

    public record Valor(
            String original,
            Multa multa,
            Juros juros,
            Desconto desconto,
            Abatimento abatimento
    ) {
    }

    public record Multa(Integer modalidade, String valorPerc) {
    }

    public record Juros(Integer modalidade, String valorPerc) {
    }

    public record Desconto(Integer modalidade, List<DescontoDataFixa> descontoDataFixa) {
    }

    public record DescontoDataFixa(String data, String valorPerc) {
    }

    public record Abatimento(Integer modalidade, String valorPerc) {
    }

    public record InfoAdicional(String nome, String valor) {
    }

    public record Loc(Long id) {
    }
}
