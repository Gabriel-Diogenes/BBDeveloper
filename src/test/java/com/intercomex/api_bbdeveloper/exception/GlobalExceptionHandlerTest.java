package com.intercomex.api_bbdeveloper.exception;

import com.intercomex.api_bbdeveloper.exception.bb.BBErrorCatalog;
import com.intercomex.api_bbdeveloper.exception.bb.BBErrorResolver;
import com.intercomex.api_bbdeveloper.exception.bb.BBErrorResponseParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        BBErrorResolver resolver = new BBErrorResolver(
                new BBErrorCatalog(objectMapper),
                new BBErrorResponseParser(objectMapper));
        handler = new GlobalExceptionHandler(resolver);
    }

    @Test
    void handleIllegalArgument_retornaCodigoEDescricao() {
        ResponseEntity<Map<String, String>> response = handler.handleIllegalArgument(
                new IllegalArgumentException("txid inválido"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDACAO", response.getBody().get("erro"));
        assertEquals("txid inválido", response.getBody().get("descricao"));
    }

    @Test
    void handleBBApi_erroConhecidoExtrato_retornaCodigoEDescricao() {
        BBApiException ex = new BBApiException(
                "Falha extrato",
                HttpStatus.BAD_REQUEST,
                "Extrato",
                "consultar extrato",
                "{\"erros\":[{\"codigo\":\"5738445.1\",\"mensagem\":\"...\"}]}");

        ResponseEntity<Map<String, String>> response = handler.handleBBApi(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("5738445.1", response.getBody().get("erro"));
        assertEquals(
                "A data final informada é superior a data atual. Refaça a requisição ajustando a data.",
                response.getBody().get("descricao"));
    }

    @Test
    void handleBBApi_erroMicrosservicoExtrato_retornaCodigo412() {
        BBApiException ex = new BBApiException(
                "Falha extrato",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Extrato",
                "consultar extrato",
                "{\"code\":\"500\",\"title\":\"Ocorreu um erro no microsserviço.\",\"detail\":\"Erro de deslocamento.\",\"errorCode\":\"412\",\"message\":\"Erro de deslocamento. Entre em contato com o suporte para resolver o problema.\",\"subErrorCode\":\"412\"}");

        ResponseEntity<Map<String, String>> response = handler.handleBBApi(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("412", response.getBody().get("erro"));
        assertEquals(
                "Parâmetros da requisição inválidos (agência, conta ou datas). Verifique os dados informados e tente novamente.",
                response.getBody().get("descricao"));
    }

    @Test
    void handleBBApi_erroDesconhecido_retornaMensagemPadrao() {
        BBApiException ex = new BBApiException(
                "Falha Pix",
                HttpStatus.BAD_GATEWAY,
                "Pix",
                "criarCob",
                "{\"erros\":[{\"codigo\":\"9999999.1\"}]}");

        ResponseEntity<Map<String, String>> response = handler.handleBBApi(ex);

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals("9999999.1", response.getBody().get("erro"));
        assertEquals(BBErrorResolver.ERRO_DESCONHECIDO, response.getBody().get("descricao"));
    }
}
