package hft.cwi.etl.filehandling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import com.cybozu.labs.langdetect.LangDetectException;

import hft.cwi.etl.crawler.WebpageData;
import hft.cwi.etl.languagedetection.LangDetector;

public class CSVHandlingUtil {
	
	private static final String TEMPORARY_FILE_NAME = "temporaryFile";
	
	
	
	static LangDetector ld;
	static {
		ld = new LangDetector();
		try {
			ld.init("profiles");
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static void writeCSVFile(Collection<WebpageData> webpageData, String csvFileName) {
		try (FileWriter csvWriter = new FileWriter(csvFileName + ".csv")){
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
			URLConnection urlConnection = webpage.getWebpage().openConnection();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		    Date date = new Date(urlConnection.getDate());
			csvWriter.append(dateFormat.format(date));
			csvWriter.append(",");
			String contentType="";
			File file = createFile(TEMPORARY_FILE_NAME,".txt", webpage.getWebPageContent());
			
			if(isHTMLFile(urlConnection)) {
				//File file = createFile(TEMPORARY_FILE_NAME,".txt", webpage.getWebPageContent());
				//addDataToCSVFile("html",csvWriter, webpage, file);
				contentType="html";
			} else if(isXMLFile(urlConnection)) {
			//	File file = createFile(TEMPORARY_FILE_NAME,".txt",webpage.getWebPageContent());
			//	addDataToCSVFile("xml",csvWriter, webpage, file);
				contentType="xml";
			} else if(isPDFFile(urlConnection)) {
				//File file = createFile(TEMPORARY_FILE_NAME,".txt",webpage.getWebPageContent());
				//addDataToCSVFile("pdf",csvWriter, webpage, file);
				contentType="pdf";
			} else {
				//File file = createFile(TEMPORARY_FILE_NAME,".txt",webpage.getWebPageContent());
				//addDataToCSVFile("other",csvWriter, webpage, file);
				contentType="other";
			}
			addDataToCSVFile(contentType,csvWriter, webpage, file);
			
			csvWriter.append("\n");
			System.out.println("append a line to csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean isPDFFile(URLConnection urlConnection) {
		return urlConnection.getContentType().contains("pdf");
	}

	private static boolean isXMLFile(URLConnection urlConnection) {
		return urlConnection.getContentType().contains("text/xml") ||
				urlConnection.getURL().toString().contains(".xml");
	}

	private static boolean isHTMLFile(URLConnection urlConnection) {
		return urlConnection.getContentType().contains("html")
				|| urlConnection.getContentType().contains("plain");
	}

	private static File createFile(String prefix, String suffix, String webPageContent) throws IOException {
		File file = File.createTempFile(prefix, suffix);
		file.deleteOnExit();
		try(FileWriter localFileWriter = new FileWriter(file)) {
			localFileWriter.append(webPageContent);
		}
		return file;
	}

	private static void addDataToCSVFile(String docType, FileWriter csvWriter, WebpageData webpage, File file) throws IOException {
		URLConnection urlConnection = webpage.getWebpage().openConnection();
		csvWriter.append(docType);
		csvWriter.append(",");
		csvWriter.append(file.getAbsolutePath());
		csvWriter.append(",");
		webpage.getAllKeywords().entrySet().forEach(entry -> {
			try {
				csvWriter.append(entry.getKey());
				csvWriter.append(",");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		csvWriter.append(urlConnection.getURL().getFile().substring(urlConnection.getURL().getFile().lastIndexOf("/")+1));
		csvWriter.append(",");
		csvWriter.append(webpage.getWebpage().toString());
		csvWriter.append(",");
		
		//add language 
		try {
			csvWriter.append(ld.detect(webpage.getWebPageContent().toString()));
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			csvWriter.append("undefined");
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
