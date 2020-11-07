package hft.cwi.etl.crawler;

import java.util.Collection;

public interface ICrawler {

	public void startCrawling(Collection<String> keywordsToLookOutFor);
	public Collection<WebpageData> getAllCrawlerData();

}
