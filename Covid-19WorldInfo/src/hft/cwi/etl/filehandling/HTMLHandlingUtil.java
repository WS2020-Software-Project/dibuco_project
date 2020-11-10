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
		StringBuilder builder = new StringBuilder();
		collectHTMLRawText(document, builder);
		return builder.toString();
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
			_allHtmlLinks.add(new URI(link.absUrl("href")));
		}
	}
	
	private static void collectHTMLRawText(Document document, StringBuilder builder) {
			Elements elements = document.getAllElements();
			System.out.println("***************************************************************************Print hmtl context***************************************************************");
			for (Element data : elements) {
				builder.append(data.text());
				System.out.println(data.text());
			}
	}
}
