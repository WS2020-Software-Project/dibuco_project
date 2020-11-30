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
			URI newuri=new URI(link.absUrl("href"));
//			if(!_allHtmlLinks.contains(newuri))				
			_allHtmlLinks.add(newuri);
		}
	}
	

}
