package hft.cwi.etl.crawler.who;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import hft.cwi.etl.crawler.Crawler;
import hft.cwi.etl.crawler.CrawlerPropertiesFilesReader;
import hft.cwi.etl.crawler.CrawlerSeed;
import hft.cwi.etl.crawler.ICrawler;
import hft.cwi.etl.crawler.WebpageData;
import hft.cwi.etl.filehandling.HTMLHandlingUtil;
import hft.cwi.etl.filehandling.PDFHandlingUtil;

public class WHOCrawler extends Crawler implements ICrawler {

	private static Collection<WebpageData> _allWebpages = new ArrayList<>();

	private static Collection<String> _forbiddenURI //
			= CrawlerPropertiesFilesReader.getForbiddenURL("whocrawlersettings.properties");

	private static Set<URI> _websiteToVisit = new HashSet<>();

	private URI _startURI;

	public WHOCrawler(URI startURI, int crawlingDeepness, int timeBufferInMs) {
		super(crawlingDeepness, timeBufferInMs);
		_startURI = startURI;
	}

	@Override
	public void startCrawling(Collection<String> keywordsToLookOutFor) {
		System.out.println("start crawling in WHO .....");
		
		final List<CrawlerSeed> theSeedList = new ArrayList<>();
		theSeedList.add(0, new CrawlerSeed(_startURI, 1));
		
		collectWebsiteURIs(theSeedList);
		
		
		
		
		_websiteToVisit.addAll(collectWebsiteURIs(theSeedList));
	
		_websiteToVisit.forEach(uri -> System.out.println(uri.toString()));
		 writeOnFile(_websiteToVisit); 
		// readURISFromFile();
		 _alreadyVisitedWebsites.clear();
	}


	protected void collectAllLinks(URI uri, String webPageContent, String docType, URLConnection urlConnection) {
		System.out.println("\n start collecting Weppage data .....\n the content is "+docType);
		_allWebpages.add(new WebpageData(uri, webPageContent, docType, urlConnection));
	}

	@Override
	public Collection<WebpageData> getAllCrawlerData() {
		return _allWebpages;
	}

	protected boolean isForbiddenLink(String uriAsString) {
		if (_forbiddenURI.stream().anyMatch(uriAsString::contains)) {
			System.out.println("remove link " + uriAsString);
		}
		return _forbiddenURI.stream().anyMatch(uriAsString::contains);
	}

	protected boolean isSameWebpage(String uriAsString) {
		return uriAsString.startsWith("https://apps.who.int/");
	}

}
