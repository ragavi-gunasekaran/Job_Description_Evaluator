package se.lexicon.resumeevaluator.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JobDescriptionEvaluationRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsNonBlankDocuments() {
        JobDescriptionEvaluationRequest request = new JobDescriptionEvaluationRequest(
                "Java and Spring Boot experience",
                "Seeking a Java developer"
        );

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void rejectsBlankDocuments() {
        JobDescriptionEvaluationRequest request = new JobDescriptionEvaluationRequest(" ", "");

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder("resumeText", "jobDescriptionText");
    }
}
