package br.com.intercomex.api_BBDeveloper.BBDeveloper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(BBApiException.class)
    public ResponseEntity<Map<String, Object>> handleBBApi(BBApiException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("erro", "Erro na API do Banco do Brasil");
        body.put("api", ex.getApi());
        body.put("operacao", ex.getOperacao());
        body.put("statusHttp", ex.getStatus().value());
        body.put("respostaBb", ex.getResponseBody());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }
}
