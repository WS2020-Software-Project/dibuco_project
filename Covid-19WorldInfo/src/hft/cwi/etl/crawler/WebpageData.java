package hft.cwi.etl.crawler;

import java.net.URI;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WebpageData {

	private Map<String,Integer> _numberOfKeywords;
	private URI _webpage;
	private String _webPageDataContent;
	private String _doctype;
	private String _date;
	
	public WebpageData(URI webpage, String webPageDataContent,String doctype, URLConnection urlConnection) {
		_webpage = webpage;
		_webPageDataContent = webPageDataContent;
		_numberOfKeywords = new HashMap<>();
		_doctype = doctype;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(urlConnection.getDate());
		_date = dateFormat.format(date);
	
	}
	
	public String getWebpageAsString() {
		return _webpage.toString();
	}
	
	public String getWebPageContent() {
		return _webPageDataContent;
	}
	
	public URI getWebpage() {
		return _webpage;
	}
	
	public String getDate() {
		return _date;
	}
	
	public String getDocType() {
		return _doctype;
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
