package hft.cwi.etl.crawler.rki;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import hft.cwi.etl.crawler.Crawler;
import hft.cwi.etl.crawler.ICrawler;
import hft.cwi.etl.crawler.WebpageData;

public class RKICrawler extends Crawler implements ICrawler{

	private static Collection<WebpageData> _allWebpages = new ArrayList<>();

	private URI _startURI;
	
	public RKICrawler(URI startURI, int crawlingDeepness, int timeBufferInMs) {
		super(crawlingDeepness, timeBufferInMs);
		_startURI = startURI;
	}

	@Override
	public void startCrawling(Collection<String> keywordsToLookOutFor) {
		// TODO Implement RKI Crawler behavior
	}

	@Override
	public Collection<WebpageData> getAllCrawlerData() {
		return _allWebpages;
	}

}
