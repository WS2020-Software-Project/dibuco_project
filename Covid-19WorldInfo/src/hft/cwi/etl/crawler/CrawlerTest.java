package hft.cwi.etl.crawler;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import hft.cwi.etl.crawler.zusammengegencorona.ZusammengegenCorona;

class CrawlerTest {

	@Test
	void test() {
		try (InputStream inputStream = new FileInputStream("testResources/crawlerTest.properties")) {
			Properties properties = new Properties();
			properties.load(inputStream);
			URI googleStartURL = createURIFromString(properties.getProperty("crawler.entrypage.google"));
			ZusammengegenCorona googleCrawler = new ZusammengegenCorona(googleStartURL, Integer.parseInt(properties.getProperty("crawler.crawlingDeepness")), 0);
			CrawlerController crawlerController = new CrawlerController(googleCrawler);
			crawlerController.executeCrawler(Arrays.asList(properties.getProperty("crawler.keywords").split(",")));
		//	assert(new File("middlewareResources/properties.csv").exists());
			
			final Stream<String> lineStream = Files.lines(Paths.get("middlewareResources/properties.csv")); 
	        assert(lineStream.anyMatch(line -> line.contains(googleStartURL.toString())));
//////////////
//	    );
	} catch (IOException e) {
		e.printStackTrace();
	}

}
	
	private static URI createURIFromString(String urlAsString) {
		try {
			return new URI(urlAsString);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
}
