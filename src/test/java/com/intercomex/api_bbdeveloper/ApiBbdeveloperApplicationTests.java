package com.intercomex.api_bbdeveloper;

import com.intercomex.api_bbdeveloper.config.TestWebClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestWebClientConfig.class)
class ApiBbdeveloperApplicationTests {

    @Test
    void contextLoads() {
    }
}
