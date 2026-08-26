package se.lexicon.resumeevaluator.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record JobDescriptionEvaluation(
        @JsonPropertyDescription("Overall match score from 0 to 100")
        int matchScore,

        @JsonPropertyDescription("Important job skills explicitly demonstrated by the resume")
        List<String> matchedSkills,

        @JsonPropertyDescription("Important job requirements not demonstrated by the resume")
        List<String> missingSkills,

        @JsonPropertyDescription("The candidate's strongest relevant qualifications")
        List<String> strengths,

        @JsonPropertyDescription("Specific, honest, actionable improvements for the resume")
        List<String> improvementSuggestions,

        @JsonPropertyDescription("A concise evidence-based conclusion")
        String summary
) {
    public JobDescriptionEvaluation {
        matchedSkills = immutableOrEmpty(matchedSkills);
        missingSkills = immutableOrEmpty(missingSkills);
        strengths = immutableOrEmpty(strengths);
        improvementSuggestions = immutableOrEmpty(improvementSuggestions);
        summary = summary == null ? "" : summary;
    }

    private static List<String> immutableOrEmpty(List<String> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
