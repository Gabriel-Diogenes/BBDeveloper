package com.intercomex.api_bbdeveloper.exception.bb;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BBErrorResponseParserTest {

    private BBErrorResponseParser parser;

    @BeforeEach
    void setUp() {
        parser = new BBErrorResponseParser(new ObjectMapper());
    }

    @Test
    void extractCodigo_errosArray() {
        Optional<String> codigo = parser.extractCodigo(
                "{\"erros\":[{\"codigo\":\"5738445.1\",\"mensagem\":\"erro\"}]}");

        assertEquals("5738445.1", codigo.orElseThrow());
    }

    @Test
    void extractCodigo_rfc7807Type() {
        Optional<String> codigo = parser.extractCodigo(
                "{\"type\":\"https://api.bb.com.br/api/v2/error/CobOperacaoInvalida\",\"status\":400}");

        assertEquals("CobOperacaoInvalida", codigo.orElseThrow());
    }

    @Test
    void extractCodigo_bodyVazio() {
        assertTrue(parser.extractCodigo(null).isEmpty());
        assertTrue(parser.extractCodigo("").isEmpty());
    }

    @Test
    void extractCodigo_errorCodeMicrosservico() {
        Optional<String> codigo = parser.extractCodigo(
                "{\"code\":\"500\",\"errorCode\":\"412\",\"subErrorCode\":\"412\",\"detail\":\"Erro de deslocamento.\"}");

        assertEquals("412", codigo.orElseThrow());
    }
}
