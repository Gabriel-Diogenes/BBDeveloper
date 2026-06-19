package com.intercomex.api_bbdeveloper.service;

import com.intercomex.api_bbdeveloper.client.extrato.ExtratoApiClient;
import com.intercomex.api_bbdeveloper.dto.extrato.response.ExtratoResponseDTO;
import com.intercomex.api_bbdeveloper.util.ExtratoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtratoService {

    private final ExtratoApiClient extratoApiClient;

    public ExtratoResponseDTO consultarExtrato(
            String agencia,
            String conta,
            String dataInicio,
            String dataFim,
            Integer pagina,
            Integer quantidadePorPagina) {

        String agenciaFormatada = ExtratoUtil.formatarAgencia(agencia);
        String contaFormatada = ExtratoUtil.formatarConta(conta);
        String dataInicioFormatada = ExtratoUtil.formatarDataSolicitacao(dataInicio);
        String dataFimFormatada = ExtratoUtil.formatarDataSolicitacao(dataFim);

        log.debug("Consultando extrato — agência: {}, conta: {}, período: {} a {}",
                agenciaFormatada, contaFormatada, dataInicioFormatada, dataFimFormatada);

        return extratoApiClient.consultar(
                agenciaFormatada,
                contaFormatada,
                dataInicioFormatada,
                dataFimFormatada,
                pagina,
                quantidadePorPagina);
    }
}
