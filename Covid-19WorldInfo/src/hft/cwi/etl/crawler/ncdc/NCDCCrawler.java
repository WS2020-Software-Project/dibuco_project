package hft.cwi.etl.crawler.ncdc;

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

public class NCDCCrawler extends Crawler implements ICrawler{
	

	private static Collection<WebpageData> _allWebpages = new ArrayList<>();

	private static Collection<String> _forbiddenURI //
			= CrawlerPropertiesFilesReader.getForbiddenURL("ncdccrawlersettings.properties");

	private static Set<URI> _websiteToVisit = new HashSet<>();

	private URI _startURI;

	public NCDCCrawler(URI startURI, int crawlingDeepness, int timeBufferInMs) {
		super(crawlingDeepness, timeBufferInMs);
		_startURI = startURI;
	}

	@Override
	public void startCrawling(Collection<String> keywordsToLookOutFor) {
		System.out.println("start crawling in NCDC .....");
		
		final List<CrawlerSeed> theSeedList = new ArrayList<>();
		theSeedList.add(0, new CrawlerSeed(_startURI, 1));
		_websiteToVisit.addAll(collectWebsiteURIs(theSeedList));
//		collectWebsiteData();
	}

	private void collectWebsiteData() {
		_websiteToVisit.stream().filter(Objects::nonNull).forEach(uri -> {
			try {
				Connection connection = Jsoup.connect(uri.toString()).maxBodySize(0);
				connection.ignoreContentType(true);
				Response response = connection.execute();
				if (response.statusCode() != 200) {
					return;
				}
				if (isXMLFile(response)) {
					collectAllLinks(uri, "xml file, no content available",XML,response.url().openConnection());
				} else if (isHTMLFile(response)) {
					Document document = connection.get();
					collectAllLinks(uri, HTMLHandlingUtil.getHTMLContent(document),HTML,response.url().openConnection());
				} else if (isPDFFile(response)) {
					collectAllLinks(uri, PDFHandlingUtil.getRawPDFData(response.url().openStream()),PDF,response.url().openConnection());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private void collectAllLinks(URI uri, String webPageContent, String docType, URLConnection urlConnection) {
		System.out.println("\n start collecting Weppage data .....\n the content is "+docType);
		
		_allWebpages.add(new WebpageData(uri, webPageContent,docType,urlConnection));
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
		return uriAsString.startsWith("https://www.ncdc.gov.in/") ||
				uriAsString.startsWith("http://www.ncdc.gov.in/"); 
	}

}
