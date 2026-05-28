package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto;

import lombok.Data;

@Data
public class PixCobrancaImediata {

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
