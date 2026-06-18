package br.com.intercomex.api_BBDeveloper.BBDeveloper.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BBCredentials {

    private String clientId;
    private String clientSecret;
    private String developerKey;
    private String sslCertPath;
    private String sslCertPassword;
    private String pixKey;
}
