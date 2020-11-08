package hft.cwi.etl.crawler.who;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import hft.cwi.etl.crawler.Crawler;
import hft.cwi.etl.crawler.ICrawler;
import hft.cwi.etl.crawler.WebpageData;
import hft.cwi.etl.filehandling.HTMLHandlingUtil;
import hft.cwi.etl.filehandling.PDFHandlingUtil;
import hft.cwi.etl.filehandling.XMLHandlingUtil;

public class WHOCrawler extends Crawler implements ICrawler {
	
	private static Collection<WebpageData> _allWebpages = new ArrayList<>();

	private URL _startURL;
	
	public WHOCrawler(URL startURL, int crawlingDeepness, int timeBufferInMs) {
		super(crawlingDeepness, timeBufferInMs);
		_startURL = startURL;
	}

	@Override
	public void startCrawling(Collection<String> keywordsToLookOutFor) {
		try {
			URLConnection urlConnection = _startURL.openConnection();
			if (isXMLFile(urlConnection)) {
				XMLHandlingUtil.getAllURLFromXML(_startURL.toString()) //
						.stream() //
						.forEach(url -> collectAllLinks(url, "xml file, it doesn't contain any relevant information"));
			} else if (isHTMLFile(urlConnection)) {
				HTMLHandlingUtil.getAllURLFromHTML(_startURL.toString()) //
						.stream().filter(Objects::nonNull) //
						.forEach(url -> collectAllLinks(url, HTMLHandlingUtil.getHTMLContent(url.toString())));
			} else if (isPDFFile(urlConnection)) {
				collectPDFFiles(_startURL, urlConnection);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void collectPDFFiles(URL startURL, URLConnection urlConnection) throws IOException {
		try {
			collectAllLinks(urlConnection.getURL().toURI(), PDFHandlingUtil.getRawPDFData(startURL.openStream()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void collectAllLinks(URI uri, String webPageContent) {
		try {
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
