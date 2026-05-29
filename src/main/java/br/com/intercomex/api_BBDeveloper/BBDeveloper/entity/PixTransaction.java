package br.com.intercomex.api_BBDeveloper.BBDeveloper.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixTransaction {

    private String txid;
    private String status;
    private String chave;
    private String valor;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String pixCopiaECola;
}
