package hft.cwi.etl.filehandling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import hft.cwi.etl.crawler.WebpageData;

public class CSVHandlingUtil {
	
	public static void writeCSVFile(Collection<WebpageData> webpageData) {
		try (FileWriter csvWriter = new FileWriter("new.csv")){
			createCSVHeaderFile(csvWriter);

			webpageData.stream().forEach(webpage -> {
				try {
					URLConnection urlConnection = webpage.getWebpage().openConnection();
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			        Date date = new Date(urlConnection.getDate());
					csvWriter.append(dateFormat.format(date));
					csvWriter.append(",");
					if(urlConnection.getContentType().contains("html")
							|| urlConnection.getContentType().contains("plain")) {
						csvWriter.append("html");
						csvWriter.append(",");
						File file = createFile("temporaryFile",".html");
						FileWriter csvWriter2 = new FileWriter(file);
						csvWriter2.append(webpage.getWebPageContent());
						csvWriter2.flush();
						csvWriter2.close();
						addDataToCSVFile(csvWriter, webpage, file);
					} else if(urlConnection.getContentType().contains("text/xml") ||
							urlConnection.getURL().toString().contains(".xml")) {
						csvWriter.append("xml");
						csvWriter.append(",");
						File file = createFile("temporaryFile",".xml");
						FileUtils.copyInputStreamToFile(webpage.getWebpage().openConnection().getInputStream(), file);
						addDataToCSVFile(csvWriter, webpage, file);
					} else if(urlConnection.getContentType().contains("pdf")) {
						csvWriter.append("pdf");
						csvWriter.append(",");
						File file = createFile("temporaryFile",".pdf");
						FileUtils.copyInputStreamToFile(webpage.getWebpage().openConnection().getInputStream(), file);
						addDataToCSVFile(csvWriter, webpage, file);
					} else {
						csvWriter.append("other");
						csvWriter.append(",");
						File file = createFile("temporaryFile",".txt");
						FileUtils.copyInputStreamToFile(webpage.getWebpage().openConnection().getInputStream(), file);
						addDataToCSVFile(csvWriter, webpage, file);
					}
					csvWriter.append("\n");
					System.out.println("append a line to csv");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			csvWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static File createFile(String prefix, String suffix) throws IOException {
		File file = File.createTempFile(prefix, suffix);
		file.deleteOnExit();
		return file;
	}

	private static void addDataToCSVFile(FileWriter csvWriter, WebpageData webpage, File file) throws IOException {
		URLConnection urlConnection = webpage.getWebpage().openConnection();
		csvWriter.append(file.getName());
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
		csvWriter.append("en");
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
