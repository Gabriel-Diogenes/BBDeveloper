package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.request;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BoletoRegistrarRequestDTO(
        Integer numeroConvenio,
        Integer numeroCarteira,
        Integer numeroVariacaoCarteira,
        Integer codigoModalidade,
        String dataEmissao,
        String dataVencimento,
        Double valorOriginal,
        String indicadorAceiteTituloVencido,
        String codigoAceite,
        Integer codigoTipoTitulo,
        String descricaoTipoTitulo,
        String indicadorPermissaoRecebimentoParcial,
        String numeroTituloBeneficiario,
        String campoUtilizacaoBeneficiario,
        String numeroTituloCliente,
        String mensagemBloquetoOcorrencia,
        String indicadorPix,
        Pagador pagador
) {
    public record Pagador(
            Integer tipoInscricao,
            String numeroInscricao,
            String nome,
            String endereco,
            String cep,
            String cidade,
            String bairro,
            String uf,
            String telefone
    ) {
    }
}
