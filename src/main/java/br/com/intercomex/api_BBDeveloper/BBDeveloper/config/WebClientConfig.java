package br.com.intercomex.api_BBDeveloper.BBDeveloper.config;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import javax.net.ssl.KeyManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class WebClientConfig {

    private final BBApiProperties properties;

    @Bean
    public WebClient bbWebClient() {
        HttpClient httpClient = HttpClient.create();

        if (properties.isWiretapEnabled()) {
            httpClient = httpClient.wiretap(
                    "reactor.netty.http.client.HttpClient",
                    LogLevel.DEBUG,
                    AdvancedByteBufFormat.TEXTUAL);
        }

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public WebClient bbMtlsWebClient() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        InputStream keyStoreStream =
                new ClassPathResource(properties.getSslCertPath()).getInputStream();

        keyStore.load(
                keyStoreStream,
                properties.getSslCertPassword().toCharArray()
        );

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm()
        );

        kmf.init(
                keyStore,
                properties.getSslCertPassword().toCharArray()
        );

        SslContext sslContext = SslContextBuilder
                .forClient()
                .keyManager(kmf)
                .build();

        HttpClient httpClient = HttpClient.create()
                .secure(t -> t.sslContext(sslContext));

        if (properties.isWiretapEnabled()) {
            httpClient = httpClient.wiretap(
                    "reactor.netty.http.client.HttpClient",
                    LogLevel.DEBUG,
                    AdvancedByteBufFormat.TEXTUAL);
        }

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
