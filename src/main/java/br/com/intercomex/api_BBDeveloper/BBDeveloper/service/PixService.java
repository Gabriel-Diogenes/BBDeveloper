package br.com.intercomex.api_BBDeveloper.BBDeveloper.service;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.BBApiClient;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.auth.TokenResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobrancaRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobvRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobrancaImediataDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobvListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobvResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.util.PixUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PixService {

    private final BBApiProperties properties;
    private final BBApiClient bbApiClient;
    private final AuthService authService;

    public PixCobrancaImediataDTO criarCobranca() {
        log.debug("Iniciando fluxo de criação de cobrança Pix (teste)");

        TokenResponseDTO token = autenticar();
        String txid = PixUtil.gerarTxid();
        log.debug("TXID gerado: {}", txid);

        return bbApiClient.criarCobrancaPix(txid, montarRequestCobTeste(), token.getAccess_token());
    }

    public PixCobrancaImediataDTO criarCob(String txid, PixCobrancaRequestDTO request) {
        String txidValido = resolverTxid(txid);
        log.debug("Criando Cob — txid: {}", txidValido);
        TokenResponseDTO token = autenticar();
        return bbApiClient.criarCobrancaPix(txidValido, request, token.getAccess_token());
    }

    public PixCobrancaImediataDTO criarCobSemTxid(PixCobrancaRequestDTO request) {
        log.debug("Criando Cob sem txid");
        TokenResponseDTO token = autenticar();
        return bbApiClient.criarCobrancaPixSemTxid(request, token.getAccess_token());
    }

    public PixCobrancaImediataDTO consultarCob(String txid) {
        PixUtil.validarTxid(txid);
        log.debug("Consultando Cob — txid: {}", txid);
        TokenResponseDTO token = autenticar();
        return bbApiClient.consultarCobrancaPix(txid, token.getAccess_token());
    }

    public PixCobrancaImediataDTO revisarCob(String txid, PixCobrancaRequestDTO request) {
        PixUtil.validarTxid(txid);
        log.debug("Revisando Cob — txid: {}", txid);
        TokenResponseDTO token = autenticar();
        return bbApiClient.revisarCobrancaPix(txid, request, token.getAccess_token());
    }

    public PixCobrancaImediataDTO cancelarCob(String txid) {
        PixUtil.validarTxid(txid);
        log.debug("Cancelando Cob — txid: {}", txid);
        PixCobrancaRequestDTO request = PixCobrancaRequestDTO.builder()
                .status("REMOVIDA_PELO_USUARIO_RECEBEDOR")
                .build();
        return revisarCob(txid, request);
    }

    public PixCobListaResponseDTO listarCobs(
            String inicio, String fim, String cpf, String cnpj, String status,
            Integer paginaAtual, Integer itensPorPagina) {
        String inicioResolvido = resolverPeriodoInicio(inicio);
        String fimResolvido = resolverPeriodoFim(fim);
        PixUtil.validarPeriodoListagem(inicioResolvido, fimResolvido);
        log.debug("Listando Cobs — período: {} a {}", inicioResolvido, fimResolvido);
        TokenResponseDTO token = autenticar();
        return bbApiClient.listarCobrancasPix(
                token.getAccess_token(), inicioResolvido, fimResolvido, cpf, cnpj, status, paginaAtual, itensPorPagina);
    }

    public PixCobvResponseDTO criarCobv(String txid, PixCobvRequestDTO request) {
        String txidValido = resolverTxid(txid);
        log.debug("Criando CobV — txid: {}", txidValido);
        TokenResponseDTO token = autenticar();
        return bbApiClient.criarCobvPix(txidValido, request, token.getAccess_token());
    }

    public PixCobvResponseDTO criarCobvSemTxid(PixCobvRequestDTO request) {
        log.debug("Criando CobV sem txid");
        TokenResponseDTO token = autenticar();
        return bbApiClient.criarCobvPixSemTxid(request, token.getAccess_token());
    }

    public PixCobvResponseDTO consultarCobv(String txid) {
        PixUtil.validarTxid(txid);
        log.debug("Consultando CobV — txid: {}", txid);
        TokenResponseDTO token = autenticar();
        return bbApiClient.consultarCobvPix(txid, token.getAccess_token());
    }

    public PixCobvResponseDTO revisarCobv(String txid, PixCobvRequestDTO request) {
        PixUtil.validarTxid(txid);
        log.debug("Revisando CobV — txid: {}", txid);
        TokenResponseDTO token = autenticar();
        return bbApiClient.revisarCobvPix(txid, request, token.getAccess_token());
    }

    public PixCobvResponseDTO cancelarCobv(String txid) {
        PixUtil.validarTxid(txid);
        log.debug("Cancelando CobV — txid: {}", txid);
        PixCobvRequestDTO request = PixCobvRequestDTO.builder()
                .status("REMOVIDA_PELO_USUARIO_RECEBEDOR")
                .build();
        return revisarCobv(txid, request);
    }

    public PixCobvListaResponseDTO listarCobvs(
            String inicio, String fim, String cpf, String cnpj, String status,
            Integer paginaAtual, Integer itensPorPagina) {
        String inicioResolvido = resolverPeriodoInicio(inicio);
        String fimResolvido = resolverPeriodoFim(fim);
        PixUtil.validarPeriodoListagem(inicioResolvido, fimResolvido);
        log.debug("Listando CobVs — período: {} a {}", inicioResolvido, fimResolvido);
        TokenResponseDTO token = autenticar();
        return bbApiClient.listarCobvPix(
                token.getAccess_token(), inicioResolvido, fimResolvido, cpf, cnpj, status, paginaAtual, itensPorPagina);
    }

    private TokenResponseDTO autenticar() {
        TokenResponseDTO token = authService.gerarToken();
        if (token == null || token.getAccess_token() == null) {
            throw new IllegalStateException("Falha ao obter token de acesso");
        }
        return token;
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

    private PixCobrancaRequestDTO montarRequestCobTeste() {
        return PixCobrancaRequestDTO.builder()
                .calendario(
                        PixCobrancaRequestDTO.Calendario.builder()
                                .expiracao(3600)
                                .build()
                )
                .devedor(
                        PixCobrancaRequestDTO.Devedor.builder()
                                .cpf("12345678910")
                                .nome("Empresa de Serviços SA")
                                .build()
                )
                .valor(
                        PixCobrancaRequestDTO.Valor.builder()
                                .original("1.00")
                                .build()
                )
                .chave(properties.getPixKey())
                .solicitacaoPagador("Pagamento de teste Pix")
                .build();
    }
}
