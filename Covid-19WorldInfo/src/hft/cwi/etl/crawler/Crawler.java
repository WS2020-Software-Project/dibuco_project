package hft.cwi.etl.crawler;

import java.net.URLConnection;

public class Crawler {

	protected boolean isXMLFile(URLConnection urlConnection) {
		return urlConnection.getContentType().contains("text/xml");
	}
	
	protected boolean isHTMLFile(URLConnection urlConnection) {
		return urlConnection.getContentType().contains("text/html")
				|| urlConnection.getContentType().contains("text/plain");
	}
	
	protected boolean isPDFFile(URLConnection urlConnection) {
		return urlConnection.getContentType().contains("pdf");
	}
	
}
