package hft.cwi.etl.crawler.who;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import hft.cwi.etl.crawler.Crawler;
import hft.cwi.etl.crawler.WebpageData;

public class WHOCrawler implements Crawler	{

	Collection<WebpageData> _allWebpages = new ArrayList<>();

	@Override
	public void startCrawling(URL startURL, Collection<String> keywordsToLookOutFor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<WebpageData> getAllCrawlerData() {
		// TODO Auto-generated method stub
		return null;
	}
	


}
