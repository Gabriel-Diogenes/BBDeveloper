package br.com.intercomex.api_BBDeveloper.BBDeveloper.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "bb")
public class BBApiProperties {

    private final Environment environment;

    @NestedConfigurationProperty
    private BBCredentials homolog = new BBCredentials();

    @NestedConfigurationProperty
    private BBCredentials producao = new BBCredentials();

    private String oauthUrl;
    private String pixBaseUrl;
    private String cobrancaBaseUrl;
    private String extratoBaseUrl;
    private String extratoHomologacaoHeader;
    private String scope;
    private boolean wiretapEnabled;
    private boolean pixRequerMtls;

    public BBApiProperties(Environment environment) {
        this.environment = environment;
    }

    public String getClientId() {
        return activeCredentials().getClientId();
    }

    public String getClientSecret() {
        return activeCredentials().getClientSecret();
    }

    public String getDeveloperKey() {
        return activeCredentials().getDeveloperKey();
    }

    public String getSslCertPath() {
        return activeCredentials().getSslCertPath();
    }

    public String getSslCertPassword() {
        return activeCredentials().getSslCertPassword();
    }

    public String getPixKey() {
        return activeCredentials().getPixKey();
    }

    public boolean isProducao() {
        return Arrays.stream(environment.getActiveProfiles()).anyMatch("producao"::equals);
    }

    public String getAmbiente() {
        return isProducao() ? "producao" : "homologacao";
    }

    private BBCredentials activeCredentials() {
        return isProducao() ? producao : homolog;
    }
}
