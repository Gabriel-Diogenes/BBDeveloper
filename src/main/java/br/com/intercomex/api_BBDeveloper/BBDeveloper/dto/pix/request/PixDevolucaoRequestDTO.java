package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PixDevolucaoRequestDTO(
        String valor,
        String natureza,
        String descricao
) {
}
