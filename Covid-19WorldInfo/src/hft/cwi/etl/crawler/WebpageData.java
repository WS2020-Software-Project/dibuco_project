package hft.cwi.etl.crawler;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WebpageData {

	private Map<String,Integer> _numberOfKeywords;
	private URL _webpage;
	
	public WebpageData(URL webpage) {
		_webpage = webpage;
		_numberOfKeywords = new HashMap<>();
	}
	
	public String getWebpageAsString() {
		return _webpage.toString();
	}
	
	public URL getWebpage() {
		return _webpage;
	}
	
	public void addKeyword(String keyword, int amountOfAppearance) {
		_numberOfKeywords.put(keyword, amountOfAppearance);
	}
	
	public void updateKeyword(String keyword, int amountOfAppearance) {
		_numberOfKeywords.computeIfPresent(keyword, (k,v) -> v = amountOfAppearance);
	}
	
	public Map<String,Integer> getAllKeywords(){
		return _numberOfKeywords;
	}
	
	public void printWebpageData() {
		System.out.println("The website URL " + _webpage.toString() + " contains the following data");
		_numberOfKeywords.entrySet().forEach(entry -> {
			System.out.println("Word " + entry.getKey() + " appeared " + entry.getValue() + " times");
		});
	}
}
