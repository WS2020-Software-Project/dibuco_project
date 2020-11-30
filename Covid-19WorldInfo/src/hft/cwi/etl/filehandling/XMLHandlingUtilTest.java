package hft.cwi.etl.filehandling;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

class XMLHandlingUtilTest {

	@Test
	void test() throws IOException {
		File input = new File("testResources/Sample.xml");
		Document doc = Jsoup.parse(input, "UTF-8");
		assert(XMLHandlingUtil.getAllURLFromXML(doc).isEmpty());
		
		}

	}


