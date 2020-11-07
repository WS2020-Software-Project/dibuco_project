package hft.cwi.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.stream.Collectors;

import hft.cwi.etl.crawler.CrawlerController;
import hft.cwi.etl.filehandling.CSVHandlingUtil;

public class Main {

	public static void main(String[] args) throws IOException {

		try (InputStream inputStream = new FileInputStream("resources/crawler.properties")) {
			Properties properties = new Properties();
			properties.load(inputStream);
			Collection<String> startEntryWebpages = Arrays
					.asList(properties.getProperty("crawler.entrypage").split(","));
			
			Collection<URL> allWebsiteEntries = startEntryWebpages.stream() //
					.map(Main::createURLFromString) //
					.collect(Collectors.toList());
					
			allWebsiteEntries.forEach(startUrl -> {
						CrawlerController crawler = new CrawlerController(startUrl);
						crawler.startCrawling(null);
						CSVHandlingUtil.writeCSVFile(crawler.getAllCrawlerData(),getURLName(startUrl));
						
					});
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
	}

	private static URL createURLFromString(String urlAsString) {
		try {
			return new URL(urlAsString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static String getURLName(URL url) {
		String urlAsString = url.toString();
		if(urlAsString.contains("who")) {
			return "who";
		} else if(urlAsString.contains("rki")) {
			return "rki";
		} else if (urlAsString.contains("ncdc")) {
			return "ncdc";
		} else {
			return "others";
		}
	}
}
