package hft.cwi.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import hft.cwi.etl.crawler.CrawlerController;
import hft.cwi.etl.crawler.ncdc.NCDCCrawler;
import hft.cwi.etl.crawler.rki.RKICrawler;
import hft.cwi.etl.crawler.who.WHOCrawler;
import hft.cwi.etl.crawler.zusammengegencorona.ZusammengegenCorona;

public class Main {

	public static void main(String[] args) {

		try (InputStream inputStream = new FileInputStream("resources/crawler.properties")) {
			Properties properties = new Properties();
			properties.load(inputStream);

			URI whoStartURL = createURIFromString(properties.getProperty("crawler.entrypage.who"));
			URI rkiStartURL = createURIFromString(properties.getProperty("crawler.entrypage.rki"));
			URI ncdcStartURL = createURIFromString(properties.getProperty("crawler.entrypage.ncdc"));
			URI zusammengegenStartURL = createURIFromString(properties.getProperty("crawler.entrypage.zusammengegen"));
			
			int crawlingDeepness = Integer.parseInt(properties.getProperty("crawler.crawlingDeepness"));
			int timeBufferInMS = Integer.parseInt(properties.getProperty("crawler.timebuffer"));

			WHOCrawler whoCrawler = new WHOCrawler(whoStartURL, crawlingDeepness, timeBufferInMS);
			RKICrawler rkiCrawler = new RKICrawler(rkiStartURL, crawlingDeepness, timeBufferInMS);
			NCDCCrawler ncdcCrawler = new NCDCCrawler(ncdcStartURL, crawlingDeepness, timeBufferInMS);
			ZusammengegenCorona zusammengegenCrawler = new ZusammengegenCorona(zusammengegenStartURL, crawlingDeepness,
					timeBufferInMS);
			
			////////////////////////
			final Collection<String> _keywordslist = Arrays.asList(properties.getProperty("crawler.keywords").split(","));
	
			CrawlerController crawlerController = new CrawlerController(zusammengegenCrawler);
			crawlerController.executeCrawler(_keywordslist);
			
//			crawlerController.changeCrawlerStrategy(ncdcCrawler);
//			crawlerController.executeCrawler(null);
//			
//			crawlerController.changeCrawlerStrategy(whoCrawler);
//			crawlerController.executeCrawler(null);
//
//			crawlerController.changeCrawlerStrategy(rkiCrawler);
//			crawlerController.executeCrawler(null);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static URI createURIFromString(String urlAsString) {
		try {
			return new URI(urlAsString);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

}
