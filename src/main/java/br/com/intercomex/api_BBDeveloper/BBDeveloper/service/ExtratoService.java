package br.com.intercomex.api_BBDeveloper.BBDeveloper.service;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.extrato.ExtratoApiClient;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.extrato.response.ExtratoResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.util.ExtratoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtratoService {

    private final ExtratoApiClient extratoApiClient;
    private final AuthService authService;

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
                authService.gerarToken().accessToken(),
                agenciaFormatada,
                contaFormatada,
                dataInicioFormatada,
                dataFimFormatada,
                pagina,
                quantidadePorPagina);
    }
}
