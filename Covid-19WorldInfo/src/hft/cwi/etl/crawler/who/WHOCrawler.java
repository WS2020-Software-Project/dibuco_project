package hft.cwi.etl.crawler.who;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hft.cwi.etl.crawler.Crawler;
import hft.cwi.etl.crawler.CrawlerPropertiesFilesReader;
import hft.cwi.etl.crawler.CrawlerSeed;
import hft.cwi.etl.crawler.ICrawler;

public class WHOCrawler extends Crawler implements ICrawler {

	private static Collection<String> _forbiddenURI //
			= CrawlerPropertiesFilesReader.getForbiddenURL("whocrawlersettings.properties");

	private URI _startURI;

	public WHOCrawler(URI startURI, int crawlingDeepness, int timeBufferInMs) {
		super(crawlingDeepness, timeBufferInMs);
		_startURI = startURI;
	}


	@Override
	public void startCrawling(Collection<String> keywordsToLookOutFor) {
		System.out.println("start crawling in WHO .....");
		List<CrawlerSeed> theSeedList = new ArrayList<>();
		theSeedList.add(0, new CrawlerSeed(_startURI, 1));
		collectWebsiteLinksAndData(theSeedList);
	}

	protected boolean isForbiddenLink(String uriAsString) {
		if (_forbiddenURI.stream().anyMatch(uriAsString::contains)) {
		}
		return _forbiddenURI.stream().anyMatch(uriAsString::contains);
	}

	protected boolean isSameWebpage(String uriAsString) {
		return uriAsString.startsWith("https://apps.who.int/");
	}

}
