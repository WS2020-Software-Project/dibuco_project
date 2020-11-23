package hft.cwi.etl.crawler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import hft.cwi.etl.filehandling.HTMLHandlingUtil;
import hft.cwi.etl.filehandling.XMLHandlingUtil;

public abstract class Crawler {

	// remove static 
	private int _crawlingDeepness;

	private int _timeBufferInMs;

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
	
	protected void readRobotFile(String propertiesFileName) {
		try (InputStream inputStream = new FileInputStream("resources/" + propertiesFileName)){
			Properties properties = new Properties();
			properties.load(inputStream);		
			
			String url = properties.getProperty("crawler.robot");
			if(url.isEmpty()) {
				return;
			}
			
			try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL( url).openStream()))){
				String line = null;
		        while((line = in.readLine()) != null) {
		            System.out.println(line);
		        }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	protected Set<URI> collectWebsiteURIs(final List<CrawlerSeed> aSeedList) {
		final Set<URI> websiteToVisit = new HashSet<>();
		try {
			if (aSeedList.size() == 0)
				return websiteToVisit;
			final CrawlerSeed currSeed = aSeedList.get(0);
			final int nextCrawlingLevel = currSeed.getLevel() + 1;
			aSeedList.remove(currSeed);

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
				if (isCrawlingDeepnessReached(nextCrawlingLevel)) {
					aSeedList.addAll(0, websiteToVisit.stream().map(uri -> new CrawlerSeed(uri, nextCrawlingLevel))
							.collect(Collectors.toList()));
				}				
			} else if (isHTMLFile(response)) {
				websiteToVisit.addAll(HTMLHandlingUtil.getAllURLFromHTML(document).stream()
						.filter(uri -> !isForbiddenLink(uri.toString()))
						.filter(uri -> isSameWebpage(uri.toString()))
						.collect(Collectors.toSet()));
				if (isCrawlingDeepnessReached(nextCrawlingLevel)) {
					aSeedList.addAll(0, websiteToVisit.stream().map(uri -> new CrawlerSeed(uri, nextCrawlingLevel))
							.collect(Collectors.toList()));
				}
				
			}
			websiteToVisit.forEach(uri -> System.out.println(uri.toString()));
						
			websiteToVisit.addAll(collectWebsiteURIs(aSeedList));
			return websiteToVisit;
			
		} catch (IOException e) {
			e.printStackTrace();
			return websiteToVisit;
		}
	}
	
	// added as abstract functions
	protected abstract boolean isForbiddenLink(String uriAsString);
	
	protected abstract boolean isSameWebpage(String uriAsString);
}
