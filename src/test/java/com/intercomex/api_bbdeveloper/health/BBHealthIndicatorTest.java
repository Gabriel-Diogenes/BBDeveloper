package com.intercomex.api_bbdeveloper.health;

import com.intercomex.api_bbdeveloper.properties.BBApiProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BBHealthIndicatorTest {

    @Mock
    private BBApiProperties properties;

    @InjectMocks
    private BBHealthIndicator healthIndicator;

    @Test
    void health_retornaUpQuandoCredenciaisConfiguradas() {
        when(properties.getClientId()).thenReturn("client-test");
        when(properties.getAmbiente()).thenReturn("homologacao");
        when(properties.isPixRequerMtls()).thenReturn(false);

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("homologacao", health.getDetails().get("ambiente"));
    }

    @Test
    void health_retornaDownSemClientId() {
        when(properties.getClientId()).thenReturn("");
        when(properties.getAmbiente()).thenReturn("producao");

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}
