package hft.cwi.etl.crawler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import java.util.TreeSet;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import hft.cwi.etl.filehandling.HTMLHandlingUtil;
import hft.cwi.etl.filehandling.PDFHandlingUtil;
import hft.cwi.etl.filehandling.XMLHandlingUtil;

public abstract class Crawler {

		// remove static 
	private int _crawlingDeepness;

	private int _timeBufferInMs;
	///////
	private int _maxChunkSize=3;
	
	public static Set<URI> _alreadyVisitedWebsites = new HashSet<>();
	
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
	////////////////////NEWCode-> read links from file ////////////////////
	protected void readURISFromFile() {
		final  Set<URI> _URISFromFile = new HashSet<>();
		String  uri;
		int counter=1;
		try (InputStream inFile = new FileInputStream("alllinksFile.txt")) {
			BufferedReader br = new BufferedReader(new InputStreamReader(inFile));
			 while ((uri = br.readLine()) != null) {
				
				if( counter<_maxChunkSize)
				{ _URISFromFile.add(new URI(uri));
					counter++;
				}
				else {
					 _URISFromFile.add(new URI(uri));
					System.out.println("\n collecting data .....\n  "+counter);
					collectAllWebsitesData(_URISFromFile);
					
					counter=1;
					_URISFromFile.clear();
				}
				 
				 
			 }
			 
			 System.out.println("\n collecting data .....\n  "+counter);
				if(!_URISFromFile.isEmpty()) {
					collectAllWebsitesData(_URISFromFile);
					counter=0;
					_URISFromFile.clear();
				}
				
			 
			 br.close();
					
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
				
			}
				
	}
	/////////////
	private void collectAllWebsitesData(Set<URI> allLinks) {
		allLinks.stream().filter(Objects::nonNull).forEach(uri -> {
			try {
				Connection connection = Jsoup.connect(uri.toString()).maxBodySize(0);
				connection.ignoreContentType(true);
				Response response = connection.execute();
				if (response.statusCode() != 200) {
					return;
				}
				if (isXMLFile(response)) {
					collectAllLinks(uri, "xml file, no content available", XML, response.url().openConnection());
				} else if (isHTMLFile(response)) {
					Document document = connection.get();
					collectAllLinks(uri, HTMLHandlingUtil.getHTMLContent(document), HTML,
							response.url().openConnection());
				} else if (isPDFFile(response)) {
					collectAllLinks(uri, PDFHandlingUtil.getRawPDFData(response.url().openStream()), PDF,
							response.url().openConnection());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	/////////
	private static Collection<WebpageData> _allWebpages = new ArrayList<>();
	
/////////////////////////////////////////////////////////////////////////////////////////////
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
			if (response.statusCode() != 200 || _alreadyVisitedWebsites.contains(currSeed.getUri())) {
				return websiteToVisit;
			}
			_alreadyVisitedWebsites.add(currSeed.getUri());
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
									
			websiteToVisit.addAll(collectWebsiteURIs(aSeedList));
			
			return websiteToVisit;
			
		} catch (IOException e) {
			e.printStackTrace();
			return websiteToVisit;
		}
	}
	
	//write all links in the file 
	protected void writeOnFile(final Set<URI>  websiteslist) {
		try (FileWriter alllinksWriter = new FileWriter("alllinksFile.txt", true);) {
			websiteslist.forEach(uri -> {
				try {
					alllinksWriter.append(uri.toString() + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			alllinksWriter.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	protected boolean isDuplicateLink(URI uriAsString,Set<URI> websiteToVisit) {

		if(websiteToVisit.isEmpty())
			return false;
		return websiteToVisit.contains(uriAsString);
	}
	// added as abstract functions
	protected abstract boolean isForbiddenLink(String uriAsString);
	
	protected abstract boolean isSameWebpage(String uriAsString);
	
	protected abstract void collectAllLinks(URI uri, String webPageContent, String docType, URLConnection urlConnection);
}
