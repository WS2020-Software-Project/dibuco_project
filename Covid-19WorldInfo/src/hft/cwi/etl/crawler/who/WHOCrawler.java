package hft.cwi.etl.crawler.who;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import hft.cwi.etl.crawler.Crawler;
import hft.cwi.etl.crawler.ICrawler;
import hft.cwi.etl.crawler.WebpageData;
import hft.cwi.etl.filehandling.HTMLHandling;

public class WHOCrawler extends Crawler implements ICrawler	{

	public static final String START_URL =  "https://apps.who.int/iris/sitemap";
	
	private static final int MAX_AMOUNTS_OF_URL_TO_VISIT = 1;
	
	Collection<WebpageData> _allWebpages = new ArrayList<>();
	
	Set<URI> _alreadyVisitedURL = new HashSet<>();
	
	@Override
	public void startCrawling(URL startURL, Collection<String> keywordsToLookOutFor) {
		try {
			URLConnection urlConnection = startURL.openConnection();
			if(isXMLFile(urlConnection)) {
				
			} else if (isHTMLFile(urlConnection)) {
				
			} else if(isPDFFile(urlConnection)) {
				
			}
			
			System.out.println(urlConnection.getContentType());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Collection<WebpageData> getAllCrawlerData() {
		return null;
	}
	
}
