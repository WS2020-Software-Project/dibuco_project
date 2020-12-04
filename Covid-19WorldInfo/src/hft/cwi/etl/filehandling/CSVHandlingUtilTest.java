package hft.cwi.etl.filehandling;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import hft.cwi.etl.crawler.WebpageData;

class CSVHandlingUtilTest {

	@Test
	void test() throws IOException, URISyntaxException {
		Collection<WebpageData> webpageData = new ArrayList<>();
		URI webpage = new URI("https://www.google.com/");
		String webPageDataContent = "empty content";
		String doctype = "html";
		URLConnection urlConnection	= webpage.toURL().openConnection();
		
		
		WebpageData testobject = new WebpageData(webpage,webPageDataContent,doctype,urlConnection);
		webpageData.add(testobject);
		CSVHandlingUtil.writeCSVFile(webpageData, "csvFileTest");
		File testFile = new File("csvFileTest.csv");
		
		assert(testFile.exists());
		testFile.delete();
		
		
		
	}

}