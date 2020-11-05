package hft.cwi.etl.filehandling;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFHandlingUtil {

	public static String getRawPDFData(InputStream inputStream) {
		String text = "";
		return getPDFFileContent(inputStream, text);
	}

	private static String getPDFFileContent(InputStream inputStream, String text) {
		try {
			File tempFile = createTemporaryFile();
			PDDocument document = copyOnlinePDFContentIntoTempFile(inputStream, tempFile);
			if (document.isEncrypted()) {
				return text;
			}
			text = collectRawPDFText(document);
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}

	private static String collectRawPDFText(PDDocument document) throws IOException {
		String text;
		PDFTextStripper stripper = new PDFTextStripper();
		text = stripper.getText(document);
		return text;
	}

	private static PDDocument copyOnlinePDFContentIntoTempFile(InputStream inputStream, File tempFile)
			throws IOException {
		FileUtils.copyInputStreamToFile(inputStream, tempFile);
		return PDDocument.load(tempFile);
	}

	private static File createTemporaryFile() throws IOException {
		File tempFile = File.createTempFile("downloadedFile", ".txt");
		tempFile.deleteOnExit();
		return tempFile;
	}

}
