package se.lexicon.resumeevaluator.service;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PrivacyRedactionService {

    static final String REDACTED = "[REDACTED]";
    private static final int CONTACT_HEADER_NON_BLANK_LINES = 12;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "(?iu)\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b"
    );
    private static final Pattern PHONE_CANDIDATE_PATTERN = Pattern.compile(
            "(?<![\\p{L}\\d])(?:\\+?\\d[\\d ().-]{6,}\\d)(?![\\p{L}\\d])"
    );
    private static final Pattern YEAR_RANGE_PATTERN = Pattern.compile("\\d{4}\\s*[-–—]\\s*\\d{4}");
    private static final Pattern HOUSE_NUMBER_PATTERN = Pattern.compile("(?iu)\\b\\d{1,5}[A-Z]?\\b");
    private static final Pattern STREET_MARKER_PATTERN = Pattern.compile(
            "(?iu)(?:\\b(?:street|st\\.?|road|rd\\.?|avenue|ave\\.?|lane|ln\\.?|drive|dr\\.?|boulevard|blvd\\.?|way|square|place|terrace)\\b|\\p{L}*(?:gatan|gata|vägen|väg|gränd|allé)\\b)"
    );
    private static final Pattern SWEDISH_POSTAL_CITY_PATTERN = Pattern.compile(
            "(?iu)^\\s*(?:SE[- ]?)?\\d{3}\\s?\\d{2}\\s+[\\p{L} .'-]{2,}\\s*$"
    );

    public String redact(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String withoutEmails = EMAIL_PATTERN.matcher(text).replaceAll(REDACTED);
        String withoutPhones = redactPhoneCandidates(withoutEmails);
        return redactLikelyContactHeaderAddresses(withoutPhones);
    }

    private String redactPhoneCandidates(String text) {
        Matcher matcher = PHONE_CANDIDATE_PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String candidate = matcher.group();
            long digitCount = candidate.chars().filter(Character::isDigit).count();
            boolean plausiblePhone = digitCount >= 8 && digitCount <= 15;
            boolean isYearRange = YEAR_RANGE_PATTERN.matcher(candidate.trim()).matches();
            matcher.appendReplacement(result, plausiblePhone && !isYearRange
                    ? Matcher.quoteReplacement(REDACTED)
                    : Matcher.quoteReplacement(candidate));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String redactLikelyContactHeaderAddresses(String text) {
        String[] lines = text.split("\\R", -1);
        int nonBlankLines = 0;

        for (int index = 0; index < lines.length; index++) {
            String line = lines[index];
            if (line.isBlank()) {
                continue;
            }

            nonBlankLines++;
            if (nonBlankLines > CONTACT_HEADER_NON_BLANK_LINES) {
                break;
            }

            if (isLikelyAddressLine(line)) {
                lines[index] = REDACTED;
            }
        }

        return String.join("\n", lines);
    }

    private boolean isLikelyAddressLine(String line) {
        if (line.length() > 160 || line.contains(REDACTED)) {
            return false;
        }

        boolean streetAddress = HOUSE_NUMBER_PATTERN.matcher(line).find()
                && STREET_MARKER_PATTERN.matcher(line).find();
        boolean swedishPostalAddress = SWEDISH_POSTAL_CITY_PATTERN.matcher(line).matches();
        return streetAddress || swedishPostalAddress;
    }
}
