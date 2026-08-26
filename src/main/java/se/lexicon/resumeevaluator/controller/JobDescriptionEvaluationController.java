package se.lexicon.resumeevaluator.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import se.lexicon.resumeevaluator.dto.JobDescriptionEvaluation;
import se.lexicon.resumeevaluator.dto.JobDescriptionEvaluationRequest;
import se.lexicon.resumeevaluator.service.JobDescriptionEvaluationService;
import se.lexicon.resumeevaluator.service.PdfTextExtractor;

@RestController
@RequestMapping("/api/resume")
public class JobDescriptionEvaluationController {

    private final JobDescriptionEvaluationService jobDescriptionEvaluationService;
    private final PdfTextExtractor pdfTextExtractor;

    public JobDescriptionEvaluationController(
            JobDescriptionEvaluationService jobDescriptionEvaluationService,
            PdfTextExtractor pdfTextExtractor
    ) {
        this.jobDescriptionEvaluationService = jobDescriptionEvaluationService;
        this.pdfTextExtractor = pdfTextExtractor;
    }

    @PostMapping(value = "/evaluate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobDescriptionEvaluation> evaluateResume(
            @Valid @RequestBody JobDescriptionEvaluationRequest request
    ) {
        JobDescriptionEvaluation evaluation = jobDescriptionEvaluationService.evaluate(
                request.resumeText(),
                request.jobDescriptionText()
        );
        return ResponseEntity.ok(evaluation);
    }

    @PostMapping(value = "/evaluate-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JobDescriptionEvaluation> evaluatePdfResume(
            @RequestPart("resumePdf") MultipartFile resumePdf,
            @RequestPart("jobDescriptionPdf") MultipartFile jobDescriptionPdf
    ) {
        String resumeText = pdfTextExtractor.extractText(resumePdf);
        String jobDescriptionText = pdfTextExtractor.extractText(jobDescriptionPdf);

        JobDescriptionEvaluation evaluation = jobDescriptionEvaluationService.evaluate(
                resumeText,
                jobDescriptionText
        );
        return ResponseEntity.ok(evaluation);
    }
}
