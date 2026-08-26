package se.lexicon.resumeevaluator.exception;

public class JobDescriptionEvaluationException extends RuntimeException {

    public JobDescriptionEvaluationException(String message) {
        super(message);
    }

    public JobDescriptionEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}
