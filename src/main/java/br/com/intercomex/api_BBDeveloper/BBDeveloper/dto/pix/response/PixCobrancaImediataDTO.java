package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobrancaRequestDTO;
import lombok.Data;

@Data
public class PixCobrancaImediataDTO {

    private String txid;
    private String revisao;
    private String location;
    private String status;
    private PixCobrancaRequestDTO.Calendario calendario;
    private PixCobrancaRequestDTO.Devedor devedor;
    private PixCobrancaRequestDTO.Valor valor;
    private String chave;
    private String solicitacaoPagador;
    private String pixCopiaECola;
}
