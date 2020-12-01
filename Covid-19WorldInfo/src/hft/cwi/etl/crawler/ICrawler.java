package hft.cwi.etl.crawler;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

public interface ICrawler {

	public void startCrawling(Collection<String> keywordsToLookOutFor);
	
	public Collection<WebpageData> getAllCrawlerData();
	
	
}
