package hft.cwi.etl.crawler;

import java.net.URL;
import java.net.URLConnection;

public abstract class Crawler {
	
	private static URL _startUrl;
	
	private static int _crawlingDeepness;
	
	private static int _timeBufferInMs;
	
	public Crawler (int crawlingDeepness, int timeBufferInMs) {
		_crawlingDeepness = crawlingDeepness;
		_timeBufferInMs = timeBufferInMs;
	}

	protected boolean isXMLFile(URLConnection urlConnection) {
		return urlConnection.getContentType().contains("text/xml") ||
				urlConnection.getURL().toString().contains(".xml");
	}
	
	protected boolean isHTMLFile(URLConnection urlConnection) {
		return urlConnection.getContentType().contains("text/html")
				|| urlConnection.getContentType().contains("text/plain");
	}
	
	protected boolean isPDFFile(URLConnection urlConnection) {
		return urlConnection.getContentType().contains("pdf");
	}
	
	protected boolean isCrawlingDeepnessReached(int deepness) {
		return _crawlingDeepness  <= deepness;
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
