package hft.cwi.etl.crawler;

import org.jsoup.Connection.Response;

public abstract class Crawler {

	private static int _crawlingDeepness;

	private static int _timeBufferInMs;

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
		return _crawlingDeepness <= deepness;
	}

	protected void waitUntilAccessNextWebsite() {
		try {
			Thread.sleep(_timeBufferInMs);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

}
