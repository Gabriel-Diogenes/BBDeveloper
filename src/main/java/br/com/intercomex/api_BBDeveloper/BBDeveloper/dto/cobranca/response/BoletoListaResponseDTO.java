package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BoletoListaResponseDTO {

    private Integer indicadorContinuidade;
    private Integer quantidadeItensPorPagina;
    private Integer quantidadeTotalItens;
    private List<BoletoResponseDTO> boletos;
}
