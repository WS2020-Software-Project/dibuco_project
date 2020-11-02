package hft.cwi.etl.filehandling;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class XMLHandlingUtil {

	private static Set<URI> _allxmlLinks = new HashSet<>();

	public static Set<URI> getAllURLFromXML(String aUrl) {
		try {
			Collection<URI> extractedLinks = collectAllSitemapLocs(Jsoup.connect(aUrl).get());
			extractedLinks.forEach(XMLHandlingUtil::collectURLFromXML);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return _allxmlLinks;
	}

	private static void collectURLFromXML(URI url) {
		try {
			if (url.toString().endsWith(".gz")) {
				File tempDonwloadedGzFile = getSitemapGZFile(url);
				File tempSiteMapFile = decompressingSitemapToXMLFile(tempDonwloadedGzFile);
				collectAllURLFromXML(tempSiteMapFile);
			} else {
				_allxmlLinks.add(url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Collection<URI> collectAllSitemapLocs(Document document) {
		Collection<URI> sitemapURIs = new ArrayList<>();
		Elements links = document.select("loc");
		for (Element link : links) {
			collectLinks(sitemapURIs, link);
		}
		sitemapURIs.forEach(uri -> System.out.println(uri.toString()));
		return sitemapURIs;
	}

	private static void collectLinks(Collection<URI> sitemapLocs, Element link) {
		try {
			sitemapLocs.add(new URI(link.text().replace("http:", "https:")));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private static File getSitemapGZFile(URI gzFileURL) throws IOException {
		File siteMapTempFile = createTemporaryFile(".xml.gz");
		siteMapTempFile.deleteOnExit();
		URLConnection connection = gzFileURL.toURL().openConnection();
		System.out.println(connection.getContentType());
		FileUtils.copyInputStreamToFile(connection.getInputStream(), siteMapTempFile);
		return siteMapTempFile;
	}

	private static File decompressingSitemapToXMLFile(File tempDonwloadedGzFile) {
		File tempSiteMapFile = null;
		try (FileInputStream fis = new FileInputStream(tempDonwloadedGzFile);
			GZIPInputStream gzis = new GZIPInputStream(fis);) {
			tempSiteMapFile = createTemporaryFile(".xml");
			tempSiteMapFile.deleteOnExit();
			FileUtils.copyInputStreamToFile(gzis, tempSiteMapFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return tempSiteMapFile;
	}

	private static void collectAllURLFromXML(File tempFile) throws IOException {
		Document sitemapDoc = Jsoup.parse(tempFile, "UTF-8");
		_allxmlLinks.addAll(collectAllSitemapLocs(sitemapDoc));
	}

	private static File createTemporaryFile(String suffix) throws IOException {
		File tempFile = File.createTempFile("data", suffix);
		tempFile.deleteOnExit();
		return tempFile;
	}

}
