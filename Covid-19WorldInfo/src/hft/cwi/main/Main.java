package hft.cwi.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import hft.cwi.etl.crawler.CrawlerController;
import hft.cwi.etl.crawler.ncdc.NCDCCrawler;
import hft.cwi.etl.crawler.rki.RKICrawler;
import hft.cwi.etl.crawler.who.WHOCrawler;

public class Main {

	public static void main(String[] args) {

		try (InputStream inputStream = new FileInputStream("resources/crawler.properties")) {
			Properties properties = new Properties();
			properties.load(inputStream);
			
			URL whoStartURL = createURLFromString(properties.getProperty("crawler.entrypage.who"));
			URL rkiStartURL = createURLFromString(properties.getProperty("crawler.entrypage.rki"));
			URL ncdcStartURL = createURLFromString(properties.getProperty("crawler.entrypage.ncdc"));
			
			int crawlingDeepness = Integer.parseInt(properties.getProperty("crawler.maxpagesvisit"));
			int timeBufferInMS =  Integer.parseInt(properties.getProperty("crawler.timebuffer"));
			
			WHOCrawler whoCrawler = new WHOCrawler(whoStartURL,crawlingDeepness,timeBufferInMS);
			RKICrawler rkiCrawler = new RKICrawler(rkiStartURL,crawlingDeepness,timeBufferInMS);
			NCDCCrawler ncdcCrawler = new NCDCCrawler(ncdcStartURL,crawlingDeepness,timeBufferInMS);
			
			CrawlerController crawlerController = new CrawlerController(whoCrawler);
			crawlerController.executeCrawler(null, properties.getProperty("crawler.csv.who"));
			
//			crawlerController.changeCrawlerStrategy(rkiCrawler);
//			crawlerController.executeCrawler(null, properties.getProperty("crawler.csv.rki"));
//			
//			crawlerController.changeCrawlerStrategy(rkiCrawler);
//			crawlerController.executeCrawler(null, properties.getProperty("crawler.csv.ncdc"));
					
			
		} catch (IOException e) {
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

}
