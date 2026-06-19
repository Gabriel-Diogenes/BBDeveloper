package com.intercomex.api_bbdeveloper.dto.pix.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PixCobrancaRequestDTO(
        Calendario calendario,
        Devedor devedor,
        Valor valor,
        String chave,
        String solicitacaoPagador,
        List<InfoAdicional> infoAdicionais,
        Loc loc,
        String status
) {
    public record Calendario(Integer expiracao) {
    }

    public record Devedor(String cpf, String cnpj, String nome) {
    }

    public record Valor(String original) {
    }

    public record InfoAdicional(String nome, String valor) {
    }

    public record Loc(Long id) {
    }
}
