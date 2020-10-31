package hft.cwi.etl.crawler;

import java.net.URL;
import java.util.Collection;

public interface Crawler {

	public void visitURL(URL url);
	public Collection<String> collectKeywords(String rawData);
	
}
