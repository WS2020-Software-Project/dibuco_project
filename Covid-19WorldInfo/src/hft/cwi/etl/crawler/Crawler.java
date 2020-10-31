package hft.cwi.etl.crawler;

import java.net.URL;
import java.util.Collection;

public interface Crawler {

	public void startCrawling(URL startURL, Collection<String> keywordsToLookOutFor);
	public Collection<WebpageData> getAllCrawlerData();
	
}
