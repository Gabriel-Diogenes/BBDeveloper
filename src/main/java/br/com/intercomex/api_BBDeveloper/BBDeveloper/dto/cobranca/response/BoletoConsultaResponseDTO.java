package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BoletoConsultaResponseDTO(
        String numero,
        String codigoLinhaDigitavel,
        String textoCodigoBarrasTituloCobranca,
        Integer numeroContratoCobranca,
        String dataEmissaoTituloCobranca,
        String dataVencimentoTituloCobranca,
        String dataRegistroTituloCobranca,
        Double valorOriginalTituloCobranca,
        Double valorAtualTituloCobranca,
        Integer codigoEstadoTituloCobranca,
        Integer numeroCarteiraCobranca,
        Integer numeroVariacaoCarteiraCobranca,
        String nomeSacadoCobranca,
        String numeroInscricaoSacadoCobranca,
        String textoCampoUtilizacaoCedente,
        String dataRecebimentoTitulo,
        Double valorPagoSacado
) {
}
