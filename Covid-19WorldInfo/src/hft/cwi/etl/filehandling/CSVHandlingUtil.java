package hft.cwi.etl.filehandling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import hft.cwi.etl.crawler.WebpageData;

public class CSVHandlingUtil {
	
	public static void writeCSVFile(Collection<WebpageData> webpageData) {
		try (FileWriter csvWriter = new FileWriter("new.csv")){
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

			webpageData.stream().forEach(webpage -> {
				
				try {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			        Date date = new Date(webpage.getWebpage().openConnection().getDate());
					csvWriter.append(dateFormat.format(date));
					csvWriter.append(",");
					
					if(webpage.getWebpage().openConnection().getContentType().contains("html")
							|| webpage.getWebpage().openConnection().getContentType().contains("plain")) {
						csvWriter.append("html");
						csvWriter.append(",");
						
						File file = File.createTempFile("asdasdasdasddasdsa", ".html");
						file.deleteOnExit();
						FileWriter csvWriter2 = new FileWriter(file);
						csvWriter2.append(webpage.getWebPageContent());
					csvWriter2.flush();
					csvWriter2.close();
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
						csvWriter.append(webpage.getWebpage().openConnection().getURL().getFile().substring(webpage.getWebpage().openConnection().getURL().getFile().lastIndexOf("/")+1));
						csvWriter.append(",");
						csvWriter.append(webpage.getWebpage().toString());
						csvWriter.append(",");
						csvWriter.append("en");
						
						
						webpage.getWebPageContent();
								
								
								
					} else if(webpage.getWebpage().openConnection().getContentType().contains("text/xml") ||
							webpage.getWebpage().openConnection().getURL().toString().contains(".xml")) {
						csvWriter.append("xml");
						csvWriter.append(",");
						File file = File.createTempFile("thisisaverylongprefix", ".xml");
						file.deleteOnExit();
						FileUtils.copyInputStreamToFile(webpage.getWebpage().openConnection().getInputStream(), file);
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
						csvWriter.append(webpage.getWebpage().openConnection().getURL().getFile().substring(webpage.getWebpage().openConnection().getURL().getFile().lastIndexOf("/")+1));
						csvWriter.append(",");
						csvWriter.append(webpage.getWebpage().toString());
						csvWriter.append(",");
						csvWriter.append("en");
					} else if(webpage.getWebpage().openConnection().getContentType().contains("pdf")) {
						csvWriter.append("pdf");
						csvWriter.append(",");
						
						File file = File.createTempFile("thisisaverylongprefix", ".pdf");
						file.deleteOnExit();
						FileUtils.copyInputStreamToFile(webpage.getWebpage().openConnection().getInputStream(), file);
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
						csvWriter.append(webpage.getWebpage().openConnection().getURL().getFile().substring(webpage.getWebpage().openConnection().getURL().getFile().lastIndexOf("/")+1,webpage.getWebpage().openConnection().getURL().getFile().lastIndexOf(".pdf") + 4));
						csvWriter.append(",");
						csvWriter.append(webpage.getWebpage().toString());
						csvWriter.append(",");
						csvWriter.append("en");
						
					} else {
						csvWriter.append("other");
						csvWriter.append(",");
						File file = File.createTempFile("thisisaverylongprefix", "txt");
						file.deleteOnExit();
						FileUtils.copyInputStreamToFile(webpage.getWebpage().openConnection().getInputStream(), file);
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
						csvWriter.append(webpage.getWebpage().openConnection().getURL().getFile().substring(webpage.getWebpage().openConnection().getURL().getFile().lastIndexOf("/")+1));
						csvWriter.append(",");
						csvWriter.append(webpage.getWebpage().toString());
						csvWriter.append(",");
						csvWriter.append("en");
					}
					csvWriter.append("\n");
					
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
}
