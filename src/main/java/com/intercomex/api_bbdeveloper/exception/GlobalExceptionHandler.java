package com.intercomex.api_bbdeveloper.exception;

import com.intercomex.api_bbdeveloper.exception.bb.BBErrorResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String CODIGO_VALIDACAO_LOCAL = "VALIDACAO";

    private final BBErrorResolver bbErrorResolver;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("erro", CODIGO_VALIDACAO_LOCAL);
        body.put("descricao", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(BBApiException.class)
    public ResponseEntity<Map<String, String>> handleBBApi(BBApiException ex) {
        BBErrorResolver.ResolvedBBError error = bbErrorResolver.resolve(ex.getApi(), ex.getResponseBody());

        Map<String, String> body = new LinkedHashMap<>();
        body.put("erro", error.codigo());
        body.put("descricao", error.descricao());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }
}
