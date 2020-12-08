package hft.cwi.etl.crawler;

import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WebpageData {

	private List<String> _keywords;
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
		_keywords = new ArrayList<>();
		_doctype = doctype;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(urlConnection.getDate());
		_date = dateFormat.format(date);
	}
	
	public WebpageData(URI webpage, InputStream inputStream,String doctype, URLConnection urlConnection) {
		_webpage = webpage;
		_inputStream = inputStream;
		_keywords = new ArrayList<>();
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
	
	public void addKeyword(String keyword) {
		_keywords.add(keyword);
	}
	
	
	public List<String> getAllKeywords(){
		return _keywords;
	}
	
	public void addAllKeywords(List<String> list) {
		_keywords.addAll(list);
	}
	
}
