package br.com.intercomex.api_BBDeveloper.BBDeveloper.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "bb")
public class BBApiProperties {

    private String clientId;
    private String clientSecret;
    private String developerKey;
    private String oauthUrl;
    private String scope;

    private String sslCertPath;
    private String sslCertPassword;

    private String pixUrl;
}
