package hft.cwi.etl.filehandling;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import hft.cwi.etl.crawler.WebpageData;
import hft.cwi.etl.languagedetection.LangDetector;

public class CSVHandlingUtil {
	
	private static final String PATH_TO_MIDDLEWARE_RESOURCE_FOLDER = "middlewareResources/";
	
	private static final String PATH_TO_MIDDLEWARE_RESOURCE_FOLDER_NAME = "middlewareResources";

	private static final String CSV_FILE_NAME = "properties.csv";
	
	private static final String HTML = "html";
	
	private static final String PDF = "pdf";

	private static LangDetector _ld = new LangDetector("profiles");
	
	public static void writeCSVFile(WebpageData webpageData) {
		File middlewareDirectory = new File(PATH_TO_MIDDLEWARE_RESOURCE_FOLDER_NAME);
		if(!middlewareDirectory.exists()) {
			middlewareDirectory.mkdir();
		}
		File file = new File(PATH_TO_MIDDLEWARE_RESOURCE_FOLDER + CSV_FILE_NAME);
		if(!file.exists()) {
			try (FileWriter csvWriter = new FileWriter(PATH_TO_MIDDLEWARE_RESOURCE_FOLDER + CSV_FILE_NAME)) {
				createCSVHeaderFile(csvWriter);
				csvWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		try (FileWriter csvWriter = new FileWriter(PATH_TO_MIDDLEWARE_RESOURCE_FOLDER + CSV_FILE_NAME,true)) {
			writeIntoCSVFile(csvWriter, webpageData);
			csvWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeIntoCSVFile(FileWriter csvWriter, WebpageData webpage) {
		try {
			UUID uuid = UUID.randomUUID();
			if(HTML.equals(webpage.getDocType())) {
				File file = createFile(PATH_TO_MIDDLEWARE_RESOURCE_FOLDER + uuid.toString() + ".html", webpage.getWebPageContent());
				csvWriter.append(webpage.getDate());
				csvWriter.append(",");
				addDataToCSVFile(webpage.getDocType(), csvWriter, webpage, file);
			} else if(PDF.equals(webpage.getDocType())) {
				File file = PDFHandlingUtil.createPDFFile(PATH_TO_MIDDLEWARE_RESOURCE_FOLDER + uuid.toString() + ".pdf",webpage.getInputStream());
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

	private static File createFile(String prefix, String webPageContent) throws IOException {
		File file = new File(prefix);
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
		csvWriter.append(file.getName());
		csvWriter.append(",");
		if (webpage.getAllKeywords().isEmpty()) {
			csvWriter.append("NO_KEYWORDS_FOUND");
			csvWriter.append(",");
		} else {
			csvWriter.append(webpage.getAllKeywords().get(0));
			csvWriter.append(",");
		}
		String websiteTitle = 
				urlConnection.getURL().getFile().substring(urlConnection.getURL().getFile().lastIndexOf("/") + 1);
		if(websiteTitle.isEmpty()) {
			csvWriter.append(UUID.randomUUID().toString());
		} else {
			csvWriter.append(websiteTitle);
		}
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
