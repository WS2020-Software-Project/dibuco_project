package hft.cwi.etl.crawler;

import java.util.Collection;

public class CrawlerController {

	private ICrawler _crawler;
	
	public CrawlerController(ICrawler crawler) {
		_crawler = crawler;
	}
	
	public void executeCrawler(Collection<String> keywordsToLookOutFor) {
		_crawler.startCrawling(keywordsToLookOutFor);
	}
	
	public void changeCrawlerStrategy(ICrawler crawler) {
		_crawler = crawler;
	}
}
