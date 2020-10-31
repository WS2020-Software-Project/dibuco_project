package hft.cwi.etl.crawler;

import java.net.URL;
import java.util.Map;

public interface Crawler {

	public void visitURL(URL url);
	public Map<String,Integer> collectKeywords(String rawData);
	
}
