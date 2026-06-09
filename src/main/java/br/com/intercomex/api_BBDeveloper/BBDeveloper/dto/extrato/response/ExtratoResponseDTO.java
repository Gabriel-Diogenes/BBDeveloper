package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.extrato.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExtratoResponseDTO(
        Integer numeroPaginaAtual,
        Integer quantidadeRegistroPaginaAtual,
        Integer numeroPaginaAnterior,
        Integer numeroPaginaProximo,
        Integer quantidadeTotalPagina,
        Integer quantidadeTotalRegistro,
        List<ExtratoLancamentoDTO> listaLancamento
) {
}
