package hft.cwi.etl.crawler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import org.jsoup.Connection.Response;

public abstract class Crawler {

	private static int _crawlingDeepness;

	private static int _timeBufferInMs;

	protected static final String XML = "xml";
	protected static final String HTML = "html";
	protected static final String PDF = "pdf";
	protected static final String OTHER = "other";

	public Crawler(int crawlingDeepness, int timeBufferInMs) {
		_crawlingDeepness = crawlingDeepness;
		_timeBufferInMs = timeBufferInMs;
	}

	protected boolean isXMLFile(Response connectionResponse) {
		return connectionResponse.contentType().contains("text/xml")
				|| connectionResponse.contentType().contains(".xml");
	}

	protected boolean isHTMLFile(Response connectionResponse) {
		return connectionResponse.contentType().contains("text/html")
				|| connectionResponse.contentType().contains("text/plain");
	}

	protected boolean isPDFFile(Response connectionResponse) {
		return connectionResponse.contentType().contains("pdf");
	}

	protected boolean isCrawlingDeepnessReached(int deepness) {
		return _crawlingDeepness <= deepness;
	}

	protected void delayCrawler() {
		try {
			Thread.sleep(_timeBufferInMs);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	protected void readRobotFile(String propertiesFileName) {
		try (InputStream inputStream = new FileInputStream("resources/" + propertiesFileName)){
			Properties properties = new Properties();
			properties.load(inputStream);		
			
			String url = properties.getProperty("crawler.robot");
			if(url.isEmpty()) {
				return;
			}
			
			try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL( url).openStream()))){
				String line = null;
		        while((line = in.readLine()) != null) {
		            System.out.println(line);
		        }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}

}
