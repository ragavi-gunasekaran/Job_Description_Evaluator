package se.lexicon.resumeevaluator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.ai.openai.api-key=test-only-placeholder")
class JobDescriptionEvaluatorApplicationTest {

    @Test
    void applicationContextLoads() {
        // Loading the context verifies Spring AI, the services, and controllers are wired together.
    }
}
