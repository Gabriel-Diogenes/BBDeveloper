package br.com.intercomex.api_BBDeveloper.BBDeveloper.exception;

public class BBApiException extends RuntimeException {

    public BBApiException(String message) {
        super(message);
    }

    public BBApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
