package hft.cwi.etl.crawler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import hft.cwi.etl.filehandling.CSVHandlingUtil;
import hft.cwi.etl.filehandling.HTMLHandlingUtil;
import hft.cwi.etl.filehandling.XMLHandlingUtil;

public abstract class Crawler {

	private int _crawlingDeepness;

	private int _timeBufferInMs;

	private static Set<URI> _alreadyVisitedWebsite = new HashSet<>();

	private static Set<URI> _alreadyUsedSeed = new HashSet<>();

	protected static final String HTML = "html";
	protected static final String PDF = "pdf";

	protected static Collection<String> _keywordslist;

	public Crawler(int crawlingDeepness, int timeBufferInMs) {
		_crawlingDeepness = crawlingDeepness;
		_timeBufferInMs = timeBufferInMs;
	}

	protected void delayCrawler() {
		try {
			Thread.sleep(_timeBufferInMs);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	protected Set<URI> collectWebsiteLinksAndData(final List<CrawlerSeed> aSeedList) {
		Set<URI> websiteToVisit = new HashSet<>();
		if (aSeedList.isEmpty()) {
			return websiteToVisit;
		}
		CrawlerSeed currSeed = aSeedList.get(0);
		int nextCrawlingLevel = currSeed.getLevel() + 1;
		aSeedList.remove(currSeed);
		if (isSeedAlreadyUsed(currSeed)) {
			websiteToVisit.addAll(collectWebsiteLinksAndData(aSeedList));
			return websiteToVisit;
		}
		_alreadyUsedSeed.add(currSeed.getUri());
		System.out.println(currSeed.getUri() + " " + currSeed.getLevel());
		try {
			delayCrawler();
			Connection connection = Jsoup.connect(currSeed.getUri().toString()).maxBodySize(0);
			connection.ignoreContentType(true);
			Response response = connection.execute();
			if (!isResponseCodeOK(response)) {
				return websiteToVisit;
			}
			Document document = createDocumentWithUTF8Encoding(response);
			collectWebsiteData(aSeedList, websiteToVisit, currSeed, nextCrawlingLevel, response, document);
			websiteToVisit.addAll(collectWebsiteLinksAndData(aSeedList));
			return websiteToVisit;
		} catch (IOException e) {
			e.printStackTrace();
			if (aSeedList.isEmpty()) {
				return websiteToVisit;
			}
			handleAndLogError(aSeedList, websiteToVisit, currSeed);
			return websiteToVisit;
		}
	}

	private boolean isResponseCodeOK(Response response) {
		return response.statusCode() == 200;
	}

	private boolean isSeedAlreadyUsed(CrawlerSeed currSeed) {
		return _alreadyUsedSeed.contains(currSeed.getUri());
	}

	private void collectWebsiteData(final List<CrawlerSeed> aSeedList, Set<URI> websiteToVisit, CrawlerSeed currSeed,
			int nextCrawlingLevel, Response response, Document document) throws IOException {
		if (isXMLFile(response)) {
			handleAndCollectXMLData(aSeedList, websiteToVisit, nextCrawlingLevel, document);
		} else if (isHTMLFile(response)) {
			handleAndSaveHTMLDataToDisk(aSeedList, websiteToVisit, currSeed, nextCrawlingLevel, response, document);
		} else if (isPDFFile(response)) {
			handleAndSavePDFDataToDisk(currSeed, response);
		}
	}

	private Document createDocumentWithUTF8Encoding(Response response) throws IOException {
		if (response.url().toString().startsWith("https://www.rki")) {
			return Jsoup.parse(HTMLHandlingUtil.getHTMLContent(response.parse()));
		} else {
			String html = IOUtils.toString(response.url().openStream(), StandardCharsets.UTF_8);
			return Jsoup.parse(html, response.url().toString());
		}
	}

	private void handleAndLogError(final List<CrawlerSeed> aSeedList, final Set<URI> websiteToVisit,
			final CrawlerSeed currSeed) {
		System.err.println("Couldn't establish connection to " + currSeed.getUri() + ". "
				+ "This website will be skipped!" + "\n Error occured at line 238 in Crawler.java");
		_alreadyVisitedWebsite.add(aSeedList.get(0).getUri());
		websiteToVisit.addAll(collectWebsiteLinksAndData(aSeedList));
	}

	private void handleAndSavePDFDataToDisk(final CrawlerSeed currSeed, Response response) throws IOException {
		if (_keywordslist.stream().anyMatch(response.url().openStream().toString()::contains)) {
			WebpageData data = new WebpageData(currSeed.getUri(), response.url().openStream(), PDF,
					response.url().openConnection());
			saveFileOnDisk(data);
		}
	}

	private void handleAndSaveHTMLDataToDisk(final List<CrawlerSeed> aSeedList, final Set<URI> websiteToVisit,
			final CrawlerSeed currSeed, final int nextCrawlingLevel, Response response, Document document)
			throws IOException {
		websiteToVisit.addAll(HTMLHandlingUtil.getAllURLFromHTML(document).stream()
				.filter(uri -> !isForbiddenLink(uri.toString())).filter(uri -> isSameWebpage(uri.toString()))
				.filter(uri -> !_alreadyVisitedWebsite.contains(uri)).collect(Collectors.toSet()));

		_alreadyVisitedWebsite.addAll(websiteToVisit);
		if (_keywordslist.stream().anyMatch(HTMLHandlingUtil.getHTMLContent(document)::contains)) {
			WebpageData data = new WebpageData(currSeed.getUri(), HTMLHandlingUtil.getHTMLContent(document),
					HTMLHandlingUtil.getHTMLContentAsText(document), HTML, response.url().openConnection());
			saveFileOnDisk(data);
		}
		if (isCrawlingDeepnessReached(nextCrawlingLevel)) {
			aSeedList.addAll(websiteToVisit.stream().map(uri -> new CrawlerSeed(uri, nextCrawlingLevel))
					.collect(Collectors.toList()));
		}
	}

	private void handleAndCollectXMLData(final List<CrawlerSeed> aSeedList, final Set<URI> websiteToVisit,
			final int nextCrawlingLevel, Document document) {
		websiteToVisit.addAll(
				XMLHandlingUtil.getAllURLFromXML(document).stream().filter(uri -> !isForbiddenLink(uri.toString()))
						.filter(uri -> isSameWebpage(uri.toString())).collect(Collectors.toSet()));
		if (isCrawlingDeepnessReached(nextCrawlingLevel)) {
			aSeedList.addAll(websiteToVisit.stream().map(uri -> new CrawlerSeed(uri, nextCrawlingLevel))
					.collect(Collectors.toList()));
		}
	}

	private void saveFileOnDisk(WebpageData data) {
		CSVHandlingUtil.writeCSVFile(data);
	}

	protected abstract boolean isForbiddenLink(String uriAsString);

	protected abstract boolean isSameWebpage(String uriAsString);

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

	protected static boolean stringContainsCOVID19Info(String webPageDataContent) {
		return _keywordslist.stream().anyMatch(webPageDataContent::contains);

	}

	protected static void assignKeywordsList(Collection<String> list) {
		_keywordslist = list;
	}
}
