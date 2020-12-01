package hft.cwi.etl.crawler;

import java.util.Collection;

import hft.cwi.etl.filehandling.CSVHandlingUtil;

public class CrawlerController {

	private ICrawler _crawler;
	
	public CrawlerController(ICrawler crawler) {
		_crawler = crawler;
	}
	
	public void executeCrawler(Collection<String> keywordsToLookOutFor,String csvFileName) {
		_crawler.startCrawling(keywordsToLookOutFor);
		
		CSVHandlingUtil.writeCSVFile(_crawler.getAllCrawlerData(),csvFileName);
	}
	
	public void changeCrawlerStrategy(ICrawler crawler) {
		_crawler = crawler;
	}
}
