package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoletoRegistrarRequestDTO {

    private Integer numeroConvenio;

    private Integer numeroCarteira;

    private Integer numeroVariacaoCarteira;

    private Integer codigoModalidade;

    private String dataEmissao;

    private String dataVencimento;

    private Double valorOriginal;

    private String indicadorAceiteTituloVencido;

    private String codigoAceite;

    private Integer codigoTipoTitulo;

    private String descricaoTipoTitulo;

    private String indicadorPermissaoRecebimentoParcial;

    private String numeroTituloBeneficiario;

    private String campoUtilizacaoBeneficiario;

    private String numeroTituloCliente;

    private String mensagemBloquetoOcorrencia;

    private String indicadorPix;

    private Pagador pagador;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagador {
        private Integer tipoInscricao;
        private String numeroInscricao;
        private String nome;
        private String endereco;
        private String cep;
        private String cidade;
        private String bairro;
        private String uf;
        private String telefone;
    }
}
