package se.lexicon.resumeevaluator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JobDescriptionEvaluationRequest(
        @NotBlank(message = "Resume text cannot be empty")
        @Size(max = 40_000, message = "Resume text cannot exceed 40000 characters")
        String resumeText,

        @NotBlank(message = "Job description cannot be empty")
        @Size(max = 40_000, message = "Job description cannot exceed 40000 characters")
        String jobDescriptionText
) {
}
