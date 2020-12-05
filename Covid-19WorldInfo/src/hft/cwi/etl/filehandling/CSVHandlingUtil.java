package hft.cwi.etl.filehandling;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import hft.cwi.etl.crawler.WebpageData;
import hft.cwi.etl.languagedetection.LangDetector;

public class CSVHandlingUtil {

	private static final String TEMPORARY_FILE_NAME = "temporaryFile";
	
	private static final String CSV_FILE_NAME = "properties.csv";
	
	private static final String HTML = "html";
	
	private static final String PDF = "pdf";

	private static LangDetector _ld = new LangDetector("profiles");
	
	public static void writeCSVFile(WebpageData webpageData) {
		File file = new File (CSV_FILE_NAME);
		if(!file.exists()) {
			try (FileWriter csvWriter = new FileWriter(CSV_FILE_NAME)) {
				createCSVHeaderFile(csvWriter);
				csvWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		try (FileWriter csvWriter = new FileWriter(CSV_FILE_NAME,true)) {
			writeIntoCSVFile(csvWriter, webpageData);
			csvWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeIntoCSVFile(FileWriter csvWriter, WebpageData webpage) {
		try {
			if(HTML.equals(webpage.getDocType())) {
				File file = createFile(TEMPORARY_FILE_NAME, ".html", webpage.getWebPageContent());
				csvWriter.append(webpage.getDate());
				csvWriter.append(",");
				addDataToCSVFile(webpage.getDocType(), csvWriter, webpage, file);
			} else if(PDF.equals(webpage.getDocType())) {
				File file = PDFHandlingUtil.createPDFFile(webpage.getInputStream());
				if(file == null) {
					return;
				}
				csvWriter.append(webpage.getDate());
				csvWriter.append(",");
				addDataToCSVFile(webpage.getDocType(), csvWriter, webpage, file);
			}
			csvWriter.append("\n");
			System.out.println("append a line to csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static File createFile(String prefix, String suffix, String webPageContent) throws IOException {
		File file = File.createTempFile(prefix, suffix);
		file.deleteOnExit();
		try (FileOutputStream fileStream = new FileOutputStream(file);
			 OutputStreamWriter writer = new OutputStreamWriter(fileStream,StandardCharsets.UTF_8.newEncoder())) {
			writer.write(webPageContent);
		}
		return file;
	}

	private static void addDataToCSVFile(String docType, FileWriter csvWriter, WebpageData webpage, File file)
			throws IOException {
		URLConnection urlConnection = webpage.getWebpage().toURL().openConnection();
		csvWriter.append(docType);
		csvWriter.append(",");
		csvWriter.append(file.getAbsolutePath());
		csvWriter.append(",");
		if (webpage.getAllKeywords().isEmpty()) {
			csvWriter.append("NO_KEYWORDS_FOUND");
			csvWriter.append(",");
		} else {
			webpage.getAllKeywords().entrySet().forEach(entry -> {
				try {
					csvWriter.append(entry.getKey());
					csvWriter.append(",");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
		csvWriter.append(
				urlConnection.getURL().getFile().substring(urlConnection.getURL().getFile().lastIndexOf("/") + 1));
		csvWriter.append(",");
		csvWriter.append(webpage.getWebpage().toString());
		csvWriter.append(",");
		if(PDF.equals(webpage.getDocType())) {
			csvWriter.append(_ld.detect(PDFHandlingUtil.getPDFFileContent(file)));
		} else if(HTML.equals(webpage.getDocType())) {
			csvWriter.append(_ld.detect(webpage.getWebpageAsText()));
		}
		
	}

	private static void createCSVHeaderFile(FileWriter csvWriter) throws IOException {
		csvWriter.append("date");
		csvWriter.append(",");
		csvWriter.append("doctype");
		csvWriter.append(",");
		csvWriter.append("filename");
		csvWriter.append(",");
		csvWriter.append("keywords");
		csvWriter.append(",");
		csvWriter.append("title");
		csvWriter.append(",");
		csvWriter.append("source_url");
		csvWriter.append(",");
		csvWriter.append("language");
		csvWriter.append("\n");
	}

}
