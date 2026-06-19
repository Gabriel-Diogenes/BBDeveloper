package com.intercomex.api_bbdeveloper.health;

import com.intercomex.api_bbdeveloper.properties.BBApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BBHealthIndicator implements HealthIndicator {

    private final BBApiProperties properties;

    @Override
    public Health health() {
        String clientId = properties.getClientId();
        boolean credenciaisOk = clientId != null && !clientId.isBlank();

        if (!credenciaisOk) {
            return Health.down()
                    .withDetail("ambiente", properties.getAmbiente())
                    .withDetail("credenciais", "client-id não configurado")
                    .build();
        }

        return Health.up()
                .withDetail("ambiente", properties.getAmbiente())
                .withDetail("aplicacao", "api_bbdeveloper")
                .withDetail("pixRequerMtls", properties.isPixRequerMtls())
                .build();
    }
}
