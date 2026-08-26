package se.lexicon.resumeevaluator.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static se.lexicon.resumeevaluator.dto.JobDescriptionEvaluation.HiringRecommendation.CONSIDER_AFTER_SCREENING;
import static se.lexicon.resumeevaluator.dto.JobDescriptionEvaluation.MatchLevel.STRONG;
import static se.lexicon.resumeevaluator.dto.JobDescriptionEvaluation.RequirementPriority.MANDATORY;
import static se.lexicon.resumeevaluator.dto.JobDescriptionEvaluation.RequirementStatus.MATCHED;

class JobDescriptionEvaluationTest {

    @Test
    void normalizesStructuredResponseAndDerivesMatchLevelFromScore() {
        JobDescriptionEvaluation.RequirementAssessment requirement =
                new JobDescriptionEvaluation.RequirementAssessment(
                        "Java 21",
                        MANDATORY,
                        MATCHED,
                        List.of("Developed services with Java 21"),
                        "The required Java version is explicitly demonstrated."
                );

        JobDescriptionEvaluation evaluation = new JobDescriptionEvaluation(
                "Maya Patel",
                "Senior Java Engineer",
                79,
                null,
                null,
                "Strong backend match with a few platform gaps.",
                List.of(requirement),
                List.of("Java 21"),
                null,
                List.of("Kafka"),
                null,
                null,
                List.of("Relevant Java experience"),
                null,
                null,
                null,
                "Recommend a technical screening."
        );

        assertThat(evaluation.matchLevel()).isEqualTo(STRONG);
        assertThat(evaluation.hiringRecommendation()).isEqualTo(CONSIDER_AFTER_SCREENING);
        assertThat(evaluation.partiallyMatchedSkills()).isEmpty();
        assertThat(evaluation.relevantExperience()).isEmpty();
        assertThat(evaluation.requirementAssessments()).containsExactly(requirement);
    }
}
