package hft.cwi.etl.crawler;

import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import hft.cwi.etl.filehandling.CSVHandlingUtil;
import hft.cwi.etl.filehandling.HTMLHandlingUtil;
import hft.cwi.etl.filehandling.PDFHandlingUtil;
import hft.cwi.etl.filehandling.XMLHandlingUtil;

public abstract class Crawler {

	private int _crawlingDeepness;

	private int _timeBufferInMs;
	
	private static Set<URI> _alreadyVisitedWebsite = new HashSet<>();
	
	private static Set<URI> _alreadyUsedSeed = new HashSet<>();
	
	protected static final String XML = "xml";
	protected static final String HTML = "html";
	protected static final String PDF = "pdf";
	protected static final String OTHER = "other";

	public Crawler(int crawlingDeepness, int timeBufferInMs) {
		_crawlingDeepness = crawlingDeepness;
		_timeBufferInMs = timeBufferInMs;
	}

	protected boolean isXMLFile(Response connectionResponse) {
		return connectionResponse.contentType().contains("text/xml")
				|| connectionResponse.contentType().contains(".xml");
	}

	protected boolean isHTMLFile(Response connectionResponse) {
		return connectionResponse.contentType().contains("text/html")
				|| connectionResponse.contentType().contains("text/plain");
	}

	protected boolean isPDFFile(Response connectionResponse) {
		return connectionResponse.contentType().contains("pdf");
	}

	protected boolean isCrawlingDeepnessReached(int deepness) {
		return deepness <= _crawlingDeepness;
	}

	protected void delayCrawler() {
		try {
			Thread.sleep(_timeBufferInMs);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	protected Set<URI> collectWebsiteURIs(final List<CrawlerSeed> aSeedList) {
		final Set<URI> websiteToVisit = new HashSet<>();
		if (aSeedList.size() == 0)
			return websiteToVisit;
		final CrawlerSeed currSeed = aSeedList.get(0);
		final int nextCrawlingLevel = currSeed.getLevel() + 1;
		aSeedList.remove(currSeed);
		
		
		if(_alreadyUsedSeed.contains(currSeed.getUri())) {
			websiteToVisit.addAll(collectWebsiteURIs(aSeedList));
			return websiteToVisit;
		}
		_alreadyVisitedWebsite.add(currSeed.getUri());		
		System.out.println(currSeed.getUri() + " " + currSeed.getLevel());
		try {
			final Connection connection = Jsoup.connect(currSeed.getUri().toString()).maxBodySize(0);
			
			connection.ignoreContentType(true);
			Response response = connection.execute();
			if (response.statusCode() != 200) {
				return websiteToVisit;
			}
			Document document = connection.get();
			
			if (isXMLFile(response)) {
				websiteToVisit.addAll(XMLHandlingUtil.getAllURLFromXML(document).stream()
						.filter(uri -> !isForbiddenLink(uri.toString()))
						.filter(uri -> isSameWebpage(uri.toString()))
						.collect(Collectors.toSet()));
				if (isCrawlingDeepnessReached(nextCrawlingLevel) ) {
					aSeedList.addAll(websiteToVisit.stream().map(uri -> new CrawlerSeed(uri, nextCrawlingLevel))
							.collect(Collectors.toList()));
				}				
			} else if (isHTMLFile(response)) {
				websiteToVisit.addAll(HTMLHandlingUtil.getAllURLFromHTML(document).stream()
						.filter(uri -> !isForbiddenLink(uri.toString()))
						.filter(uri -> isSameWebpage(uri.toString()))
						.filter(uri -> !_alreadyVisitedWebsite.contains(uri))
						.collect(Collectors.toSet()));
				_alreadyVisitedWebsite.addAll(websiteToVisit);
				saveFileOnDisk(currSeed.getUri() ,HTMLHandlingUtil.getHTMLContent(document), HTML,response.url().openConnection());
				if (isCrawlingDeepnessReached(nextCrawlingLevel)) {
					aSeedList.addAll(websiteToVisit.stream().map(uri -> new CrawlerSeed(uri, nextCrawlingLevel))
							.collect(Collectors.toList()));
				}
				
			} else if (isPDFFile(response)) {
				saveFileOnDisk(currSeed.getUri() ,PDFHandlingUtil.getRawPDFData(response.url().openStream()), PDF,response.url().openConnection());
			}
			
			websiteToVisit.addAll(collectWebsiteURIs(aSeedList));
			
			return websiteToVisit;
			
		} catch (IOException e) {
			if(aSeedList.isEmpty()) {
				return websiteToVisit;
			}
			System.err.println("Couldn't establish connection to " + currSeed.getUri() + ". "
					+ "This website will be skipped!"
					+ "\n Error occured at line 238 in Crawler.java");
			_alreadyVisitedWebsite.add(aSeedList.get(0).getUri());	
			websiteToVisit.addAll(collectWebsiteURIs(aSeedList));
			return websiteToVisit;
		}
	}
	
	public void saveFileOnDisk(URI uri, String webPageContent, String docType, URLConnection urlConnection){
		WebpageData data = new WebpageData(uri, webPageContent, docType, urlConnection);
		CSVHandlingUtil.writeCSVFile(data);
	}
	
	protected abstract boolean isForbiddenLink(String uriAsString);
	
	protected abstract boolean isSameWebpage(String uriAsString);
	
}
