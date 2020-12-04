package hft.cwi.etl.filehandling;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFHandlingUtil {

	public static File createPDFFile(InputStream inputStream) {
		try {
			File tempFile = createTemporaryFile();
			FileUtils.copyInputStreamToFile(inputStream, tempFile);
			inputStream.close();
			return tempFile;
		} catch (IOException e) {
			return null;
		}
	}

	public static String getPDFFileContent(File file) {
		String text = "";
		try {
			PDDocument document = PDDocument.load(file);
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

	private static File createTemporaryFile() throws IOException {
		File tempFile = File.createTempFile("downloadedFile", ".pdf");
		tempFile.deleteOnExit();
		return tempFile;
	}

}
