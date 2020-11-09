package hft.cwi.etl.languagedetection;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.cybozu.labs.langdetect.LangDetectException;

class LangDetectorTest {

	@Test
	void test() {
		LangDetector ld= new LangDetector();
		try {
			ld.init("profiles");
			assert(ld.detect("This is an English language test!").equalsIgnoreCase("en") &&
					ld.detect("هذا اختبار لغة عربية!").equalsIgnoreCase("ar") &&
					ld.detect("Das ist ein deutscher Sprachtest!").equalsIgnoreCase("de") &&
					ld.detect("Ceci est un test de langue française!").equalsIgnoreCase("fr"));
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

}
