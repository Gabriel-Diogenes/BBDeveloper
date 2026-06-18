package br.com.intercomex.api_BBDeveloper.BBDeveloper;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.config.TestWebClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestWebClientConfig.class)
class BbDeveloperApplicationTests {

    @Test
    void contextLoads() {
    }
}
