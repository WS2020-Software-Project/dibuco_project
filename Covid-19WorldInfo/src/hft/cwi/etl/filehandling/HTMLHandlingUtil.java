package hft.cwi.etl.filehandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLHandlingUtil {
	
	
	private static Set<URI> _allHtmlLinks = new HashSet<>();

	public static String getHTMLContent(Document document) {
		return document.text();
	}

	public static Set<URI> getAllURLFromHTML(Document document) {
		try {
			collectAllURL(document);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return _allHtmlLinks;
	}
	
	private static void collectAllURL(Document document) throws URISyntaxException {
		Elements links = document.select("a[href]");
		for (Element link : links) {
			String uri = link.absUrl("href").replace(" ", "-").replace("../", "");
			if(uri.endsWith("/")) {
				URI newuri  = new URI(uri.substring(0, uri.length() - 1));
				_allHtmlLinks.add(newuri);
			} else {
				URI newuri = new URI(uri);
				_allHtmlLinks.add(newuri);
			}
		
		}
	}
}
