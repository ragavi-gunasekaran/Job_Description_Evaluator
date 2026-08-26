package se.lexicon.resumeevaluator.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JobDescriptionEvaluationPromptTest {

    @Test
    void promptRequiresCompleteEvidenceBasedRequirementCoverage() {
        assertThat(JobDescriptionEvaluationService.SYSTEM_PROMPT)
                .contains("every material job requirement")
                .contains("PARTIALLY_MATCHED")
                .contains("NOT_DEMONSTRATED")
                .contains("influence the score or recommendation")
                .contains("Never invent or assume");
    }

    @Test
    void userPromptKeepsBothWorkshopPlaceholders() {
        assertThat(JobDescriptionEvaluationService.USER_PROMPT)
                .contains("{resumeText}")
                .contains("{jobDescriptionText}")
                .contains("requirement assessments");
    }
}
