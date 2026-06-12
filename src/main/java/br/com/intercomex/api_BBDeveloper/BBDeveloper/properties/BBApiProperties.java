package br.com.intercomex.api_BBDeveloper.BBDeveloper.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "bb")
public class BBApiProperties {

    private String clientId;
    private String clientSecret;
    private String developerKey;
    private String oauthUrl;
    private String pixBaseUrl;
    private String cobrancaBaseUrl;
    private String extratoBaseUrl;
    private String extratoHomologacaoHeader;
    private String scope;
    private String sslCertPath;
    private String sslCertPassword;
    private String pixKey;
    private boolean wiretapEnabled;
}
