package br.com.intercomex.api_BBDeveloper.BBDeveloper.dto;

import lombok.Data;

@Data
public class TokenResponseDTO {

    private String access_token;

    private String token_type;

    private Integer expires_in;

    private String scope;
}
