package hft.cwi.etl.crawler.ncdc;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import hft.cwi.etl.crawler.Crawler;
import hft.cwi.etl.crawler.ICrawler;
import hft.cwi.etl.crawler.WebpageData;

public class NCDCCrawler extends Crawler implements ICrawler{
	
	private static Collection<WebpageData> _allWebpages = new ArrayList<>();

	private URL _startURL;
	
	public NCDCCrawler(URL startURL, int crawlingDeepness, int timeBufferInMs) {
		super(crawlingDeepness, timeBufferInMs);
		_startURL = startURL;
	}

	@Override
	public void startCrawling(Collection<String> keywordsToLookOutFor) {
		// TODO Implement NCDCCrawler behavior
	}

	@Override
	public Collection<WebpageData> getAllCrawlerData() {
		return _allWebpages;
	}

}
