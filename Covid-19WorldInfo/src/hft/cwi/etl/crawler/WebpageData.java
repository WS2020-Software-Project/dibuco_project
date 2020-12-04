package hft.cwi.etl.crawler;

import java.io.InputStream;
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
	private InputStream _inputStream;
	private String _contentAsText;
	private String _webPageDataContent;
	private String _doctype;
	private String _date;
	
	public WebpageData(URI webpage, String webPageDataContent,String contentAsText,String doctype, URLConnection urlConnection) {
		_webpage = webpage;
		_webPageDataContent = webPageDataContent;
		_contentAsText = contentAsText;
		_numberOfKeywords = new HashMap<>();
		_doctype = doctype;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(urlConnection.getDate());
		_date = dateFormat.format(date);
	}
	
	public WebpageData(URI webpage, InputStream inputStream,String doctype, URLConnection urlConnection) {
		_webpage = webpage;
		_inputStream = inputStream;
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
	
	public InputStream getInputStream() {
		return _inputStream;
	}
	
	public URI getWebpage() {
		return _webpage;
	}
	
	public String getWebpageAsText() {
		return _contentAsText;
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
	
}
