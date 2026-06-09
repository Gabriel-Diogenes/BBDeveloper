package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobvRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PixCobvResponseDTO(
        String txid,
        Integer revisao,
        String location,
        String status,
        Calendario calendario,
        PixCobvRequestDTO.Devedor devedor,
        PixCobvRequestDTO.Valor valor,
        String chave,
        String solicitacaoPagador,
        String pixCopiaECola,
        Recebedor recebedor
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Calendario(String criacao, String dataDeVencimento, Integer validadeAposVencimento) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Recebedor(String cpf, String cnpj, String nome, String logradouro, String cidade, String uf, String cep) {
    }
}
