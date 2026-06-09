package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PixCobvListaResponseDTO {

    private Object parametros;
    private List<PixCobvResponseDTO> cobsv;
}
