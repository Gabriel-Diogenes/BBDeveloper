package br.com.intercomex.api_BBDeveloper.BBDeveloper.service;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.client.BBApiClient;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.auth.TokenResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobrancaImediataDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobrancaRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.util.PixUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service de Pix.
 * 
 * Responsável por regras de negócio relacionadas a Pix.
 * Delega chamadas HTTP para o BBApiClient.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PixService {

    private final BBApiProperties properties;
    private final BBApiClient bbApiClient;
    private final AuthService authService;

    /**
     * Cria uma cobrança Pix imediata.
     * 
     * Fluxo:
     * 1. Obtém token de autenticação
     * 2. Gera TXID único
     * 3. Monta requisição Pix
     * 4. Envia ao BB Developer
     * 
     * @return PixCobrancaImediata com dados da cobrança criada
     * @throws IllegalStateException se falhar na autenticação
     */
    public PixCobrancaImediataDTO criarCobranca() {
        log.debug("Iniciando fluxo de criação de cobrança Pix");
        
        // 1. Autentica
        TokenResponseDTO token = authService.gerarToken();
        
        if (token == null || token.getAccess_token() == null) {
            log.error("Falha crítica: não foi possível obter token para criar cobrança");
            throw new IllegalStateException("Falha ao obter token de acesso");
        }

        // 2. Gera TXID único
        String txid = PixUtil.gerarTxid();
        log.debug("TXID gerado: {}", txid);

        // 3. Monta requisição
        PixCobrancaRequestDTO request = PixCobrancaRequestDTO.builder()
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

        // 4. Envia ao cliente HTTP
        return bbApiClient.criarCobrancaPix(txid, request, token.getAccess_token());
    }
}
