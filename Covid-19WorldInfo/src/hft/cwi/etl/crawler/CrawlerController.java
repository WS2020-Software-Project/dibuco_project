package hft.cwi.etl.crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import hft.cwi.etl.filehandling.HTMLHandlingUtil;
import hft.cwi.etl.filehandling.PDFHandlingUtil;
import hft.cwi.etl.filehandling.XMLHandlingUtil;

public class CrawlerController extends Crawler implements ICrawler{

	private static final int MAX_AMOUNTS_OF_URL_TO_VISIT = 1;
	
	private static String _languange = "";

	Collection<WebpageData> _allWebpages = new ArrayList<>();
	
	public CrawlerController(String languange) {
		_languange = languange;
	}

	public CrawlerController() {
		// default constructor
	}

	Set<URI> _alreadyVisitedURL = new HashSet<>();

	@Override
	public void startCrawling(URL startURL, Collection<String> keywordsToLookOutFor) {
		try {
			
			URLConnection urlConnection = startURL.openConnection();
			if (isXMLFile(urlConnection)) {
				XMLHandlingUtil.getAllURLFromXML(startURL.toString()) //
				.stream() //
				.forEach(url -> collectAllLinks(url,null));
				
			} else if (isHTMLFile(urlConnection)) {
				// loop check html content
				HTMLHandlingUtil.getHTMLContent(startURL.toString());
				HTMLHandlingUtil.getAllURLFromHTML(startURL.toString()) //
						.stream()
						.filter(Objects::nonNull) //
						.forEach(url -> collectAllLinks(url,HTMLHandlingUtil.getHTMLContent(url.toString())));
				_allWebpages.forEach(webpages -> System.out.println(webpages.getWebpage().toString()));
			} else if (isPDFFile(urlConnection)) {
				collectPDFFiles(startURL, urlConnection);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void collectPDFFiles(URL startURL, URLConnection urlConnection) throws IOException {
		try {
			collectAllLinks(urlConnection.getURL().toURI(), PDFHandlingUtil.getRawPDFData(startURL.openStream()));
			System.out.println(PDFHandlingUtil.getRawPDFData(startURL.openStream()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void collectAllLinks(URI uri, String webPageContent){
		try {
			System.out.println(webPageContent);
			_allWebpages.add(new WebpageData(uri.toURL(), webPageContent));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Collection<WebpageData> getAllCrawlerData() {
		return _allWebpages;
	}
}
