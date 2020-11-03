package hft.cwi.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import hft.cwi.etl.crawler.CrawlerController;
import hft.cwi.etl.filehandling.CSVHandlingUtil;

public class Main {

	public static void main(String[] args) throws IOException {

		try (InputStream inputStream = new FileInputStream("resources/crawler.properties")) {
			Properties properties = new Properties();
			properties.load(inputStream);
			Collection<String> startEntryWebpages = Arrays
					.asList(properties.getProperty("crawler.entrypage").split(","));
			CrawlerController crawler = new CrawlerController();
			startEntryWebpages.stream() //
					.map(Main::createURLFromString) //
					.forEach(startUrl -> crawler.startCrawling(startUrl, null));
			
			CSVHandlingUtil.writeCSVFile(crawler.getAllCrawlerData());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
	}

	public static URL createURLFromString(String urlAsString) {
		try {
			return new URL(urlAsString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
