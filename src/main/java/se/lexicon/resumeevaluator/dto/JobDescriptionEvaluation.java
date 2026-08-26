package se.lexicon.resumeevaluator.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record JobDescriptionEvaluation(
        @JsonPropertyDescription("Candidate name explicitly stated in the resume, or 'Not provided'")
        String candidateName,

        @JsonPropertyDescription("Target job title explicitly stated or clearly indicated by the job description")
        String targetJobTitle,

        @JsonPropertyDescription("Overall match score from 0 to 100")
        int matchScore,

        @JsonPropertyDescription("Score band derived from matchScore")
        MatchLevel matchLevel,

        @JsonPropertyDescription("Evidence-based recommendation about whether the candidate should proceed")
        HiringRecommendation hiringRecommendation,

        @JsonPropertyDescription("Concise overview of fit, most important evidence, and most important gaps")
        String executiveSummary,

        @JsonPropertyDescription("One assessment for every material requirement in the job description")
        List<RequirementAssessment> requirementAssessments,

        @JsonPropertyDescription("Exact skills and requirements demonstrated by explicit resume evidence")
        List<String> matchedSkills,

        @JsonPropertyDescription("Requirements supported by related, transferable, incomplete, or unclear evidence")
        List<String> partiallyMatchedSkills,

        @JsonPropertyDescription("Important job skills or requirements not demonstrated in the resume")
        List<String> missingSkills,

        @JsonPropertyDescription("Relevant roles, responsibilities, achievements, and years of experience from the resume")
        List<String> relevantExperience,

        @JsonPropertyDescription("Relevant education, courses, and certifications, including missing required credentials")
        List<String> educationAndCertifications,

        @JsonPropertyDescription("Candidate's strongest job-relevant qualifications supported by evidence")
        List<String> strengths,

        @JsonPropertyDescription("Material evidence gaps, inconsistencies, or hiring risks that need clarification")
        List<String> concerns,

        @JsonPropertyDescription("Targeted interview questions that verify partial matches and important missing evidence")
        List<String> interviewQuestions,

        @JsonPropertyDescription("Specific, honest, job-targeted improvements for the resume")
        List<String> improvementSuggestions,

        @JsonPropertyDescription("Final evidence-based assessment explaining the recommendation")
        String finalAssessment
) {
    public JobDescriptionEvaluation {
        candidateName = textOrDefault(candidateName, "Not provided");
        targetJobTitle = textOrDefault(targetJobTitle, "Not provided");
        matchLevel = MatchLevel.fromScore(matchScore);
        hiringRecommendation = hiringRecommendation == null
                ? HiringRecommendation.CONSIDER_AFTER_SCREENING
                : hiringRecommendation;
        executiveSummary = textOrDefault(executiveSummary, "");
        requirementAssessments = immutableOrEmpty(requirementAssessments);
        matchedSkills = immutableOrEmpty(matchedSkills);
        partiallyMatchedSkills = immutableOrEmpty(partiallyMatchedSkills);
        missingSkills = immutableOrEmpty(missingSkills);
        relevantExperience = immutableOrEmpty(relevantExperience);
        educationAndCertifications = immutableOrEmpty(educationAndCertifications);
        strengths = immutableOrEmpty(strengths);
        concerns = immutableOrEmpty(concerns);
        interviewQuestions = immutableOrEmpty(interviewQuestions);
        improvementSuggestions = immutableOrEmpty(improvementSuggestions);
        finalAssessment = textOrDefault(finalAssessment, "");
    }

    private static <T> List<T> immutableOrEmpty(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    private static String textOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    public enum MatchLevel {
        EXCELLENT,
        STRONG,
        MODERATE,
        LOW;

        public static MatchLevel fromScore(int score) {
            if (score >= 85) {
                return EXCELLENT;
            }
            if (score >= 70) {
                return STRONG;
            }
            if (score >= 50) {
                return MODERATE;
            }
            return LOW;
        }
    }

    public enum HiringRecommendation {
        STRONGLY_RECOMMEND_INTERVIEW,
        RECOMMEND_INTERVIEW,
        CONSIDER_AFTER_SCREENING,
        NOT_RECOMMENDED
    }

    public enum RequirementPriority {
        MANDATORY,
        PREFERRED,
        OTHER
    }

    public enum RequirementStatus {
        MATCHED,
        PARTIALLY_MATCHED,
        NOT_DEMONSTRATED
    }

    public record RequirementAssessment(
            @JsonPropertyDescription("A distinct material requirement from the job description")
            String requirement,

            @JsonPropertyDescription("Whether the job description presents this as mandatory, preferred, or other")
            RequirementPriority priority,

            @JsonPropertyDescription("How completely the resume demonstrates this requirement")
            RequirementStatus status,

            @JsonPropertyDescription("Concrete resume statements supporting the status; empty when not demonstrated")
            List<String> resumeEvidence,

            @JsonPropertyDescription("Brief explanation of the match, partial match, or evidence gap")
            String explanation
    ) {
        public RequirementAssessment {
            requirement = textOrDefault(requirement, "Not provided");
            priority = priority == null ? RequirementPriority.OTHER : priority;
            status = status == null ? RequirementStatus.NOT_DEMONSTRATED : status;
            resumeEvidence = immutableOrEmpty(resumeEvidence);
            explanation = textOrDefault(explanation, "");
        }
    }
}
