package com.intercomex.api_bbdeveloper.exception.bb;

public record BBErrorEntry(
        String codigo,
        Integer statusHttp,
        String descricao) {
}
