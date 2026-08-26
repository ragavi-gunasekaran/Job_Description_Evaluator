package se.lexicon.resumeevaluator.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import se.lexicon.resumeevaluator.exception.InvalidPdfException;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PdfTextExtractorTest {

    private final PdfTextExtractor extractor = new PdfTextExtractor();

    @Test
    void extractsTextFromPdf() throws Exception {
        MockMultipartFile pdf = new MockMultipartFile(
                "resumePdf",
                "resume.pdf",
                "application/pdf",
                createPdf("Java Spring Boot Developer")
        );

        assertThat(extractor.extractText(pdf)).contains("Java Spring Boot Developer");
    }

    @Test
    void rejectsAFileThatOnlyPretendsToBePdf() {
        MockMultipartFile fakePdf = new MockMultipartFile(
                "resumePdf",
                "resume.pdf",
                "application/pdf",
                "not a pdf".getBytes()
        );

        assertThatThrownBy(() -> extractor.extractText(fakePdf))
                .isInstanceOf(InvalidPdfException.class)
                .hasMessageContaining("not a valid PDF");
    }

    private byte[] createPdf(String text) throws Exception {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                content.newLineAtOffset(72, 720);
                content.showText(text);
                content.endText();
            }

            document.save(output);
            return output.toByteArray();
        }
    }
}
