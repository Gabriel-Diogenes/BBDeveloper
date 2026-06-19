package com.intercomex.api_bbdeveloper.dto.cobranca.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BoletoAlterarRequestDTO(
        Integer numeroConvenio,
        String indicadorNovaDataVencimento,
        AlteracaoData alteracaoData,
        String indicadorNovoValorNominal,
        AlteracaoValor alteracaoValor,
        String indicadorAtribuirDesconto,
        Desconto desconto,
        String indicadorAlterarDesconto,
        AlteracaoDesconto alteracaoDesconto,
        String indicadorAlterarDataDesconto,
        AlteracaoDataDesconto alteracaoDataDesconto,
        String indicadorProtestar,
        Protesto protesto,
        String indicadorSustacaoProtesto,
        String indicadorCancelarProtesto,
        String indicadorIncluirAbatimento,
        Abatimento abatimento,
        String indicadorAlterarAbatimento,
        AlteracaoAbatimento alteracaoAbatimento,
        String indicadorCobrarJuros,
        Juros juros,
        String indicadorDispensarJuros,
        String indicadorCobrarMulta,
        Multa multa,
        String indicadorDispensarMulta,
        String indicadorNegativar,
        Negativacao negativacao,
        String indicadorAlterarSeuNumero,
        AlteracaoSeuNumero alteracaoSeuNumero,
        String indicadorAlterarEnderecoPagador,
        AlteracaoEndereco alteracaoEndereco,
        String indicadorAlterarPrazoBoletoVencido
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AlteracaoData(String novaDataVencimento) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AlteracaoValor(String novoValorNominal) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Desconto(
            String tipoPrimeiroDesconto,
            String valorPrimeiroDesconto,
            String percentualPrimeiroDesconto,
            String dataExpiracaoPrimeiroDesconto,
            String tipoSegundoDesconto,
            String valorSegundoDesconto,
            String percentualSegundoDesconto,
            String dataExpiracaoSegundoDesconto,
            String tipoTerceiroDesconto,
            String valorTerceiroDesconto,
            String percentualTerceiroDesconto,
            String dataExpiracaoTerceiroDesconto
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AlteracaoDesconto(
            String tipoPrimeiroDesconto,
            String novoValorPrimeiroDesconto,
            String novoPercentualPrimeiroDesconto,
            String novaDataLimitePrimeiroDesconto,
            String tipoSegundoDesconto,
            String novoValorSegundoDesconto,
            String novoPercentualSegundoDesconto,
            String novaDataLimiteSegundoDesconto,
            String tipoTerceiroDesconto,
            String novoValorTerceiroDesconto,
            String novoPercentualTerceiroDesconto,
            String novaDataLimiteTerceiroDesconto
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AlteracaoDataDesconto(
            String novaDataLimitePrimeiroDesconto,
            String novaDataLimiteSegundoDesconto,
            String novaDataLimiteTerceiroDesconto
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Protesto(String quantidadeDiasProtesto) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Abatimento(String valorAbatimento) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AlteracaoAbatimento(String novoValorAbatimento) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Juros(String tipoJuros, String valorJuros, String percentualJuros) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Multa(String tipoMulta, String valorMulta, String percentualMulta) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Negativacao(Integer quantidadeDiasNegativacao, String tipoNegativacao) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AlteracaoSeuNumero(String codigoSeuNumero) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AlteracaoEndereco(
            String enderecoPagador,
            String bairroPagador,
            String cidadePagador,
            String ufPagador,
            String cepPagador
    ) {
    }
}
