package hft.cwi.etl.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

public class CrawlerPropertiesFilesReader {

	private static Properties _properties;
	
	private static Collection<String> _forbiddenURL;
	
	public static Collection<String> getForbiddenURL(String propertiesFileName) {
		
		try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFileName)){
			_properties = new Properties();
			_properties.load(inputStream);
			_forbiddenURL = Arrays.asList(_properties.getProperty("notAllowedWebpagePatterns").split(","));
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
		return _forbiddenURL;
	}
}
