package se.lexicon.resumeevaluator.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PrivacyRedactionServiceTest {

    private final PrivacyRedactionService service = new PrivacyRedactionService();

    @Test
    void redactsEmailPhoneAndLikelyHeaderAddressWithoutRemovingExperienceYears() {
        String resume = """
                Ada Andersson
                ada@example.com
                +46 70 123 45 67
                Storgatan 12
                123 45 Stockholm

                Senior Java Developer
                Experience: 2018-2024
                Built Spring Boot services.
                """;

        String redacted = service.redact(resume);

        assertThat(redacted)
                .doesNotContain("ada@example.com")
                .doesNotContain("+46 70 123 45 67")
                .doesNotContain("Storgatan 12")
                .doesNotContain("123 45 Stockholm")
                .contains("[REDACTED]")
                .contains("2018-2024")
                .contains("Built Spring Boot services.");
    }

    @Test
    void leavesOrdinarySkillContentUntouched() {
        String text = "Java 21, Spring Boot, REST APIs, Kafka and AWS";

        assertThat(service.redact(text)).isEqualTo(text);
    }
}
