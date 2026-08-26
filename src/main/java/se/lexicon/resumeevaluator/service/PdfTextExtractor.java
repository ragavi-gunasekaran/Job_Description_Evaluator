package se.lexicon.resumeevaluator.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import se.lexicon.resumeevaluator.exception.InvalidPdfException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class PdfTextExtractor {

    private static final byte[] PDF_SIGNATURE = "%PDF-".getBytes(StandardCharsets.US_ASCII);

    public String extractText(MultipartFile file) {
        validateUpload(file);

        try {
            byte[] bytes = file.getBytes();
            validatePdfSignature(bytes);

            try (PDDocument document = Loader.loadPDF(bytes)) {
                if (document.isEncrypted()) {
                    throw new InvalidPdfException("Password-protected PDFs are not supported");
                }

                String extractedText = new PDFTextStripper().getText(document);
                if (extractedText == null || extractedText.isBlank()) {
                    throw new InvalidPdfException(
                            "The PDF contains no extractable text. Image-only PDFs require OCR."
                    );
                }
                return extractedText;
            }
        } catch (InvalidPdfException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new InvalidPdfException("The uploaded PDF could not be read", exception);
        }
    }

    private void validateUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidPdfException("PDF file cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        boolean pdfFilename = originalFilename != null
                && originalFilename.toLowerCase(Locale.ROOT).endsWith(".pdf");
        boolean pdfContentType = contentType == null || "application/pdf".equalsIgnoreCase(contentType);

        if (!pdfFilename || !pdfContentType) {
            throw new InvalidPdfException("Only PDF files are supported");
        }
    }

    private void validatePdfSignature(byte[] bytes) {
        if (bytes.length < PDF_SIGNATURE.length) {
            throw new InvalidPdfException("The uploaded file is not a valid PDF");
        }

        for (int index = 0; index < PDF_SIGNATURE.length; index++) {
            if (bytes[index] != PDF_SIGNATURE[index]) {
                throw new InvalidPdfException("The uploaded file is not a valid PDF");
            }
        }
    }
}
