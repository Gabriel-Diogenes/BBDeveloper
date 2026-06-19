package com.intercomex.api_bbdeveloper.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleIllegalArgument_retorna400ComMensagem() {
        ResponseEntity<Map<String, String>> response = handler.handleIllegalArgument(
                new IllegalArgumentException("txid inválido"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("txid inválido", response.getBody().get("erro"));
    }

    @Test
    void handleBBApi_propagaStatusERespostaBb() {
        BBApiException ex = new BBApiException(
                "Falha Pix",
                HttpStatus.BAD_GATEWAY,
                "Pix",
                "criarCob",
                "{\"erros\":[{\"codigo\":\"502\"}]}");

        ResponseEntity<Map<String, Object>> response = handler.handleBBApi(ex);

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals("Pix", response.getBody().get("api"));
        assertEquals("criarCob", response.getBody().get("operacao"));
        assertEquals(502, response.getBody().get("statusHttp"));
        assertEquals("{\"erros\":[{\"codigo\":\"502\"}]}", response.getBody().get("respostaBb"));
    }
}
