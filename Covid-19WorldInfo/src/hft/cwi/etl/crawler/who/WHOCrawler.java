package hft.cwi.etl.crawler.who;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import hft.cwi.etl.crawler.Crawler;
import hft.cwi.etl.crawler.CrawlerPropertiesFilesReader;
import hft.cwi.etl.crawler.ICrawler;
import hft.cwi.etl.crawler.WebpageData;
import hft.cwi.etl.filehandling.HTMLHandlingUtil;
import hft.cwi.etl.filehandling.PDFHandlingUtil;
import hft.cwi.etl.filehandling.XMLHandlingUtil;

public class WHOCrawler extends Crawler implements ICrawler {

	private static Collection<WebpageData> _allWebpages = new ArrayList<>();

	private static Collection<String> _forbiddenURI //
			= CrawlerPropertiesFilesReader.getForbiddenURL("whocrawlersettings.properties");

	private static Set<URI> _websiteToVisit = new HashSet<>();

	private static Collection<URI> _websitesVisited = new ArrayList<>();

	private URI _startURI;

	public WHOCrawler(URI startURI, int crawlingDeepness, int timeBufferInMs) {
		super(crawlingDeepness, timeBufferInMs);
		_startURI = startURI;
	}

	@Override
	public void startCrawling(Collection<String> keywordsToLookOutFor) {
		try {
			Connection connection = Jsoup.connect(_startURI.toString()).maxBodySize(0);
			connection.ignoreContentType(true);
			Response response = connection.execute();
			if (response.statusCode() != 200) {
				return;
			}
			if (isXMLFile(response)) {
				Document document = connection.get();
				_websiteToVisit.addAll(XMLHandlingUtil.getAllURLFromXML(document));
				_websiteToVisit.forEach(uri -> System.out.println(uri.toString()));
			} else if (isHTMLFile(response)) {
				Document document = connection.get();
				Collection<URI> websiteToVisit = HTMLHandlingUtil.getAllURLFromHTML(document).stream() //
						.filter(uri -> !isForbiddenLink(uri.toString())) //
						.filter(uri -> isSameWebpage(uri.toString())) //
						.collect(Collectors.toList());

				_websiteToVisit.addAll(websiteToVisit);
				_websiteToVisit.forEach(uri -> System.out.println(uri.toString()));
			} else if (isPDFFile(response)) {
				_websiteToVisit.add(response.url().toURI());
				System.out.println(PDFHandlingUtil.getRawPDFData(response.url().openStream()));
			}

			collectWebsiteData();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
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
		_allWebpages.add(new WebpageData(uri, webPageContent,docType,urlConnection));
	}

	@Override
	public Collection<WebpageData> getAllCrawlerData() {
		return _allWebpages;
	}

	private boolean isForbiddenLink(String uriAsString) {
		if (_forbiddenURI.stream().anyMatch(uriAsString::contains)) {
			System.out.println("remove link " + uriAsString);
		}
		return _forbiddenURI.stream().anyMatch(uriAsString::contains);
	}

	private boolean isSameWebpage(String uriAsString) {
		return uriAsString.startsWith("https://apps.who.int/");
	}

}
