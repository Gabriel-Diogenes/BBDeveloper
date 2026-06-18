package br.com.intercomex.api_BBDeveloper.BBDeveloper.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
@Profile("test")
public class TestWebClientConfig {

    @Bean
    @Primary
    public WebClient bbWebClient() {
        return WebClient.builder().build();
    }

    @Bean
    @Primary
    public WebClient bbMtlsWebClient() {
        return WebClient.builder().build();
    }
}
