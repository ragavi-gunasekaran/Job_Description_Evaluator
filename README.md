# Job Description Evaluator

A Spring Boot and Spring AI API that evaluates a resume against a job description and maps the model response to a structured Java record.

## Requirements

- Java 21 or newer
- Maven 3.6.3 or newer (IntelliJ's bundled Maven is sufficient)
- An OpenAI API key

## IntelliJ setup

1. Open this folder as a Maven project and allow IntelliJ to import `pom.xml`.
2. Open `JobDescriptionEvaluatorApplication.java` and click its green Run button once.
3. Open **Run → Edit Configurations** and select `JobDescriptionEvaluatorApplication`.
4. If necessary, choose **Modify options → Environment variables**.
5. Add an environment variable named `OPENAI_API_KEY` and paste the real key as its value.
6. Click **Apply**, then **OK**, and restart the application.

Do not put the real key in `application.properties`, source code, screenshots, or Git. The
checked-in configuration contains only `spring.ai.openai.api-key=${OPENAI_API_KEY}`.

The application listens on `http://localhost:8080`.

Starting the application proves that the environment variable is available. Sending an
evaluation request is what verifies that the key is valid and permitted to call the model.

## Text endpoint

Send `POST /api/resume/evaluate` with `Content-Type: application/json`:

```json
{
  "resumeText": "Ragavi Gunasekaran - Senior Java Developer with seven years of experience. Built Java 17 and Spring Boot services and designed REST APIs for financial systems.",
  "jobDescriptionText": "Senior Java Engineer. Mandatory: Java 17, Spring Boot, REST API design, and production Kafka experience."
}
```

The successful response is a detailed structured JSON object. Each important job
requirement receives its own assessment instead of being hidden inside a general summary:

```json
{
  "candidateName": "Ragavi Gunasekaran",
  "targetJobTitle": "Senior Java Engineer",
  "matchScore": 78,
  "matchLevel": "STRONG",
  "hiringRecommendation": "RECOMMEND_INTERVIEW",
  "executiveSummary": "Ragavi demonstrates the core Java, Spring Boot, and REST requirements, but the resume does not demonstrate production Kafka experience.",
  "requirementAssessments": [
    {
      "requirement": "Java 17",
      "priority": "MANDATORY",
      "status": "MATCHED",
      "resumeEvidence": ["Built Java 17 services"],
      "explanation": "The required Java version is explicitly demonstrated."
    },
    {
      "requirement": "Spring Boot",
      "priority": "MANDATORY",
      "status": "MATCHED",
      "resumeEvidence": ["Built Spring Boot services"],
      "explanation": "The framework requirement is explicitly demonstrated."
    },
    {
      "requirement": "REST API design",
      "priority": "MANDATORY",
      "status": "MATCHED",
      "resumeEvidence": ["Designed REST APIs for financial systems"],
      "explanation": "The resume provides direct REST API design evidence."
    },
    {
      "requirement": "Production Kafka experience",
      "priority": "MANDATORY",
      "status": "NOT_DEMONSTRATED",
      "resumeEvidence": [],
      "explanation": "The resume contains no Kafka evidence; this does not prove the candidate lacks the skill."
    }
  ],
  "matchedSkills": ["Java 17", "Spring Boot", "REST API design"],
  "partiallyMatchedSkills": [],
  "missingSkills": ["Production Kafka experience"],
  "relevantExperience": ["Seven years of Java development", "Financial systems experience"],
  "educationAndCertifications": [],
  "strengths": ["Direct evidence for three central mandatory requirements"],
  "concerns": ["Kafka experience is not demonstrated"],
  "interviewQuestions": ["Have you used Kafka in a production system? If so, describe your responsibilities."],
  "improvementSuggestions": ["Add Kafka projects and measurable outcomes only if they reflect real experience."],
  "finalAssessment": "Recommend an interview, with Kafka experience verified during screening."
}
```

The exact score and wording can vary between calls.

## Different Postman scenarios

Use these separately to verify that the evaluator handles different people, roles,
experience levels, and match strengths. Every example uses the same text endpoint.

### Scenario 1: strong direct match — Maya Patel

```json
{
  "resumeText": "Ragavi Gunasekaran - Senior Java Developer with seven years of experience. Built Java 17 and Spring Boot microservices, REST APIs, Kafka consumers, Docker images, and AWS deployments.",
  "jobDescriptionText": "Senior Java Engineer. Must have five years of Java, Spring Boot, microservices, REST APIs, Kafka, Docker, and AWS. Kubernetes is preferred."
}
```

Expected focus: a high score, many direct matches, and Kubernetes as a preferred gap.

### Scenario 2: transferable but incomplete match — Erik Lund

```json
{
  "resumeText": "Stefi - Backend Developer with four years of Java, Spring Boot, PostgreSQL, Docker, GitHub Actions, and AWS application deployments. Supported production incidents with the platform team.",
  "jobDescriptionText": "Cloud DevOps Engineer. Mandatory: Terraform, Kubernetes, Linux administration, CI/CD ownership, AWS infrastructure, monitoring, and incident response. Java experience is preferred."
}
```

Expected focus: transferable AWS, Docker, CI/CD, and incident experience, but material
Terraform, Kubernetes, Linux, monitoring, and infrastructure-ownership gaps.

### Scenario 3: career-change and evidence gaps — Sofia Martinez

```json
{
  "resumeText": "Rekha - Customer Support Specialist with three years of experience. Created weekly Excel reports, tracked service KPIs, resolved customer issues, and completed an introductory SQL course.",
  "jobDescriptionText": "Junior Data Analyst. Required: SQL, Excel, data cleaning, dashboard creation, statistics, and presenting insights to stakeholders. Power BI or Tableau is preferred."
}
```

Expected focus: useful Excel, reporting, and communication evidence; partial SQL evidence;
and missing proof for data cleaning, dashboards, statistics, and BI tools.

## PDF endpoint

Send `POST /api/resume/evaluate-pdf` as `multipart/form-data` with two file parts:

- `resumePdf`: the resume PDF
- `jobDescriptionPdf`: the job description PDF

Only text-based PDFs are supported. Scanned image-only documents require OCR and are rejected with a clear error. Extracted email addresses, phone numbers, and conservative contact-header address matches are replaced with `[REDACTED]` before the text is sent to OpenAI.

## API-key troubleshooting

- `Could not resolve placeholder 'OPENAI_API_KEY'`: add the variable to the same IntelliJ run configuration that starts the application, then restart it.
- HTTP `401`: the API key is invalid, revoked, expired, or copied incorrectly.
- HTTP `429`: the account may have reached a quota, rate, or billing limit.
- HTTP `502` from this application: read the IntelliJ console for the underlying provider error.
- If the instructor supplied a custom base URL or required model name, add those values only after confirming the exact settings with the instructor.

## Run tests

From IntelliJ, run the tests under `src/test/java`, or use IntelliJ's bundled Maven with the `test` goal.
