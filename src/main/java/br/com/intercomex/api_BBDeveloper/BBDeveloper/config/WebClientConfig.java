package br.com.intercomex.api_BBDeveloper.BBDeveloper.config;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final BBApiProperties config;

    @Bean
    public WebClient bbWebClient() throws Exception {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        InputStream keyStoreStream =
                new ClassPathResource(config.getSslCertPath())
                        .getInputStream();

        keyStore.load(
                keyStoreStream,
                config.getSslCertPassword().toCharArray()
        );

        KeyManagerFactory kmf =
                KeyManagerFactory.getInstance(
                        KeyManagerFactory.getDefaultAlgorithm()
                );

        kmf.init(
                keyStore,
                config.getSslCertPassword().toCharArray()
        );

        SslContext sslContext = SslContextBuilder
                .forClient()
                .keyManager(kmf)
                .build();

        HttpClient httpClient = HttpClient.create()
                .secure(t -> t.sslContext(sslContext));

        return WebClient.builder()
                .clientConnector(
                        new ReactorClientHttpConnector(httpClient)
                )
                .build();
    }
}
