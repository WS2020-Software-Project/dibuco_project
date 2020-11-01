package hft.cwi.main;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import hft.cwi.etl.crawler.rki.RKICrawler;
import hft.cwi.etl.crawler.who.WHOCrawler;

public class Main {

	private static String _pdfURL = "https://apps.who.int/iris/bitstream/handle/10665/331917/COVID-19-infection-prevention-during-transfer-and-transport-eng.pdf?sequence=1&isAllowed=y";

	private static String _htmlURL = "https://www.who.int";

	public static void main(String[] args) throws IOException {
		WHOCrawler whoCrawler = new WHOCrawler();
		RKICrawler rkiCrawler = new RKICrawler();
		try {
			whoCrawler.startCrawling(new URL(_pdfURL), null);
			whoCrawler.startCrawling(new URL(_htmlURL), null);

			rkiCrawler.startCrawling(new URL(RKICrawler.START_URL), null);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
