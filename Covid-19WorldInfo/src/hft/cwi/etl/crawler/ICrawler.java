package hft.cwi.etl.crawler;

import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;

public interface ICrawler {

	public void startCrawling(URL startURL, Collection<String> keywordsToLookOutFor);
	public Collection<WebpageData> getAllCrawlerData();

}
