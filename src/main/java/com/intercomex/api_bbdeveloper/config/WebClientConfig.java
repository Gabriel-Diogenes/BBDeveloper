package com.intercomex.api_bbdeveloper.config;

import com.intercomex.api_bbdeveloper.properties.BBApiProperties;
import com.intercomex.api_bbdeveloper.service.AuthService;
import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import javax.net.ssl.KeyManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.time.Duration;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class WebClientConfig {

    private final BBApiProperties properties;

    @Bean
    public WebClient bbOAuthWebClient() {
        return webClientBuilder(plainHttpClient()).build();
    }

    @Bean
    public WebClient bbWebClient(@Lazy AuthService authService) {
        return webClientBuilder(plainHttpClient())
                .filter(BBWebClientFilters.bearerAuthWithRetry(authService))
                .build();
    }

    @Bean
    public WebClient bbMtlsWebClient(@Lazy AuthService authService) throws Exception {
        return webClientBuilder(mtlsHttpClient())
                .filter(BBWebClientFilters.bearerAuthWithRetry(authService))
                .build();
    }

    private WebClient.Builder webClientBuilder(HttpClient httpClient) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    private HttpClient plainHttpClient() {
        return configureTimeouts(HttpClient.create());
    }

    private HttpClient mtlsHttpClient() throws Exception {
        SslContext ssl = sslContext();
        return configureTimeouts(HttpClient.create().secure(spec -> spec.sslContext(ssl)));
    }

    private HttpClient configureTimeouts(HttpClient httpClient) {
        httpClient = httpClient
                .responseTimeout(Duration.ofMillis(properties.getReadTimeoutMs()))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeoutMs());

        if (properties.isWiretapEnabled()) {
            httpClient = httpClient.wiretap(
                    "reactor.netty.http.client.HttpClient",
                    LogLevel.DEBUG,
                    AdvancedByteBufFormat.TEXTUAL);
        }
        return httpClient;
    }

    private SslContext sslContext() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        try (InputStream keyStoreStream = new ClassPathResource(properties.getSslCertPath()).getInputStream()) {
            keyStore.load(keyStoreStream, properties.getSslCertPassword().toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, properties.getSslCertPassword().toCharArray());

        return SslContextBuilder.forClient().keyManager(kmf).build();
    }
}
