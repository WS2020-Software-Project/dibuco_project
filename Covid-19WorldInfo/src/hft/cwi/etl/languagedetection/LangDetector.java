package hft.cwi.etl.languagedetection;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

public class  LangDetector {
	
	public LangDetector(String profileDirectory) {
		 try {
			DetectorFactory.loadProfile(profileDirectory);
		} catch (LangDetectException e) {
			e.printStackTrace();
		}
	}
    public String detect(String text) {
        Detector detector;
		try {
			detector = DetectorFactory.create();
		    detector.append(text);
	        return detector.detect();
		} catch (LangDetectException e) {
			return "undefined languange";
		}
    
    }
}