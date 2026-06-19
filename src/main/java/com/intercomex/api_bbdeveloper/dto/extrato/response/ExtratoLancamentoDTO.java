package com.intercomex.api_bbdeveloper.dto.extrato.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExtratoLancamentoDTO(
        String indicadorTipoLancamento,
        Long dataLancamento,
        Long dataMovimento,
        Integer codigoAgenciaOrigem,
        Integer numeroLote,
        Long numeroDocumento,
        Integer codigoHistorico,
        String textoDescricaoHistorico,
        Double valorLancamento,
        String indicadorSinalLancamento,
        String textoInformacaoComplementar,
        Long numeroCpfCnpjContrapartida,
        String indicadorTipoPessoaContrapartida,
        Integer codigoBancoContrapartida,
        Integer codigoAgenciaContrapartida,
        String numeroContaContrapartida,
        String textoDvContaContrapartida
) {
}
