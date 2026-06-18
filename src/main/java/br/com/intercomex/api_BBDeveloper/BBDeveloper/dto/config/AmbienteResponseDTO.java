package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.config;

public record AmbienteResponseDTO(
        String ambiente,
        String oauthUrl,
        String pixBaseUrl,
        String cobrancaBaseUrl,
        String extratoBaseUrl,
        boolean extratoRequerMtls,
        boolean extratoHeaderHomologacaoAtivo,
        boolean pixRequerMtls
) {
}
