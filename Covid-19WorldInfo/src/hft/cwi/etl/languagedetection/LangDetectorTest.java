package hft.cwi.etl.languagedetection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LangDetectorTest {

	private static LangDetector _ld;

	@BeforeAll
	public static void init() {
		_ld = new LangDetector("profiles");
	}

	@Test
	public void testENLanguange() {
		assert (_ld.detect("This is an English language test!").equalsIgnoreCase("en"));
	}

	@Test
	public void testARLanguange() {
		assert (_ld.detect("هذا اختبار لغة عربي!").equalsIgnoreCase("ar"));
	}

	@Test
	public void testDELanguange() {
		assert (_ld.detect("Das ist ein deutscher Sprachtest!").equalsIgnoreCase("de"));
	}

	@Test
	public void testFRLanguange() {
		assert (_ld.detect("Ceci est un test de langue franÃ§aise!").equalsIgnoreCase("fr"));
	}

}
