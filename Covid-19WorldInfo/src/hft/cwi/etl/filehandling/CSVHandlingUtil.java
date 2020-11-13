package hft.cwi.etl.filehandling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Collection;

import hft.cwi.etl.crawler.WebpageData;
import hft.cwi.etl.languagedetection.LangDetector;

public class CSVHandlingUtil {

	private static final String TEMPORARY_FILE_NAME = "temporaryFile";

	private static LangDetector _ld = new LangDetector("profiles");

	public static void writeCSVFile(Collection<WebpageData> webpageData, String csvFileName) {
		try (FileWriter csvWriter = new FileWriter(csvFileName + ".csv")) {
			createCSVHeaderFile(csvWriter);
			webpageData.stream() //
					.forEach(webpage -> createCSVFile(csvWriter, webpage));
			csvWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createCSVFile(FileWriter csvWriter, WebpageData webpage) {
		try {
			csvWriter.append(webpage.getDate());
			csvWriter.append(",");
			File file = createFile(TEMPORARY_FILE_NAME, ".txt", webpage.getWebPageContent());
			addDataToCSVFile(webpage.getDocType(), csvWriter, webpage, file);
			csvWriter.append("\n");
			System.out.println("append a line to csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static File createFile(String prefix, String suffix, String webPageContent) throws IOException {
		File file = File.createTempFile(prefix, suffix);
		file.deleteOnExit();
		try (FileWriter localFileWriter = new FileWriter(file)) {
			localFileWriter.append(webPageContent);
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
		csvWriter.append(_ld.detect(webpage.getWebPageContent()));
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
