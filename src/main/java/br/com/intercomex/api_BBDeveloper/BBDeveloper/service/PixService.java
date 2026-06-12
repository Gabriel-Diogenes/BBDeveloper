package br.com.intercomex.api_BBDeveloper.BBDeveloper.service;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.pix.PixApiClient;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.auth.TokenResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobrancaRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobvRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobrancaImediataDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobvListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobvResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.util.PixUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PixService {

    private final PixApiClient pixApiClient;
    private final AuthService authService;

    public PixCobrancaImediataDTO criarCob(String txid, PixCobrancaRequestDTO request) {
        String txidValido = resolverTxid(txid);
        log.debug("Criando Cob — txid: {}", txidValido);
        return pixApiClient.criarCob(txidValido, request, bearer());
    }

    public PixCobrancaImediataDTO criarCobSemTxid(PixCobrancaRequestDTO request) {
        log.debug("Criando Cob sem txid");
        return pixApiClient.criarCobSemTxid(request, bearer());
    }

    public PixCobrancaImediataDTO consultarCob(String txid) {
        PixUtil.validarTxid(txid);
        log.debug("Consultando Cob — txid: {}", txid);
        return pixApiClient.consultarCob(txid, bearer());
    }

    public PixCobrancaImediataDTO revisarCob(String txid, PixCobrancaRequestDTO request) {
        PixUtil.validarTxid(txid);
        log.debug("Revisando Cob — txid: {}", txid);
        return pixApiClient.revisarCob(txid, request, bearer());
    }

    public PixCobListaResponseDTO listarCobs(
            String inicio, String fim, String cpf, String cnpj, String status,
            Integer paginaAtual, Integer itensPorPagina) {
        String inicioResolvido = resolverPeriodoInicio(inicio);
        String fimResolvido = resolverPeriodoFim(fim);
        PixUtil.validarPeriodoListagem(inicioResolvido, fimResolvido);
        log.debug("Listando Cobs — período: {} a {}", inicioResolvido, fimResolvido);
        return pixApiClient.listarCobs(
                bearer(), inicioResolvido, fimResolvido, cpf, cnpj, status, paginaAtual, itensPorPagina);
    }

    public PixCobvResponseDTO criarCobv(String txid, PixCobvRequestDTO request) {
        String txidValido = resolverTxid(txid);
        log.debug("Criando CobV — txid: {}", txidValido);
        return pixApiClient.criarCobv(txidValido, request, bearer());
    }

    public PixCobvResponseDTO consultarCobv(String txid) {
        PixUtil.validarTxid(txid);
        log.debug("Consultando CobV — txid: {}", txid);
        return pixApiClient.consultarCobv(txid, bearer());
    }

    public PixCobvResponseDTO revisarCobv(String txid, PixCobvRequestDTO request) {
        PixUtil.validarTxid(txid);
        log.debug("Revisando CobV — txid: {}", txid);
        return pixApiClient.revisarCobv(txid, request, bearer());
    }

    public PixCobvListaResponseDTO listarCobvs(
            String inicio, String fim, String cpf, String cnpj, String status,
            Integer paginaAtual, Integer itensPorPagina) {
        String inicioResolvido = resolverPeriodoInicio(inicio);
        String fimResolvido = resolverPeriodoFim(fim);
        PixUtil.validarPeriodoListagem(inicioResolvido, fimResolvido);
        log.debug("Listando CobVs — período: {} a {}", inicioResolvido, fimResolvido);
        return pixApiClient.listarCobvs(
                bearer(), inicioResolvido, fimResolvido, cpf, cnpj, status, paginaAtual, itensPorPagina);
    }

    private String bearer() {
        TokenResponseDTO token = authService.gerarToken();
        return token.accessToken();
    }

    private String resolverTxid(String txid) {
        if (txid == null || txid.isBlank()) {
            return PixUtil.gerarTxid();
        }
        PixUtil.validarTxid(txid);
        return txid;
    }

    private String resolverPeriodoInicio(String inicio) {
        return (inicio == null || inicio.isBlank()) ? PixUtil.periodoInicioPadrao() : inicio;
    }

    private String resolverPeriodoFim(String fim) {
        return (fim == null || fim.isBlank()) ? PixUtil.periodoFimPadrao() : fim;
    }
}
