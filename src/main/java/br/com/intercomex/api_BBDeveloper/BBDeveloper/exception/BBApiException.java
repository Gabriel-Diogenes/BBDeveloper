package br.com.intercomex.api_BBDeveloper.BBDeveloper.exception;

import org.springframework.http.HttpStatus;

public class BBApiException extends RuntimeException {

    private final HttpStatus status;
    private final String api;
    private final String operacao;
    private final String responseBody;

    public BBApiException(String message) {
        this(message, null, null, null, null);
    }

    public BBApiException(String message, HttpStatus status, String api, String operacao, String responseBody) {
        super(message);
        this.status = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
        this.api = api;
        this.operacao = operacao;
        this.responseBody = responseBody;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getApi() {
        return api;
    }

    public String getOperacao() {
        return operacao;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
