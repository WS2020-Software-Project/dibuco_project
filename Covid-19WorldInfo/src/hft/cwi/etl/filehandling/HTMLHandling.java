package hft.cwi.etl.filehandling;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLHandling {

	private static Set<URI> _allHtmlLinks = new HashSet<>();

	public static Set<URI> getAllURLFromHTML(String url) {
		try {
			collectAllURL(Jsoup.connect(url).get());
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return _allHtmlLinks;
	}

	private static void collectAllURL(Document document) throws URISyntaxException {
		Elements links = document.select("a[href]");
		for (Element link : links) {
			_allHtmlLinks.add(new URI(link.absUrl("href")));
		}
	}
}
