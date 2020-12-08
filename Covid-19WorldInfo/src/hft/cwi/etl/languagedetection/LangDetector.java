package hft.cwi.etl.languagedetection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

public class  LangDetector {
	
	private static Collection<String> _allProfilesName = Arrays.asList("af","ar","bg","bn","cs","da","de","el","en","es"
			,"et","fa","fi","fr","gu","he","hi","hr","hu","id","it","ja","kn","ko","lt","lv","mk","ml","mr","ne","nl","no","pa","pl","pt","ro"
			,"ru","sk","sl","so","sq","sv","sw","ta","te","th","tl","tr","uk","ur","vi","zh-cn","zh-tw");
	
	public LangDetector(String profileDirectory) {
		 try {
			File directory = new File(profileDirectory);
			if(!directory.exists()) {
				directory.mkdir();
				_allProfilesName.stream().forEach(name -> createProfile(profileDirectory, name));
			}
			DetectorFactory.loadProfile(profileDirectory);
		} catch (LangDetectException e) {
			e.printStackTrace();
		}
	}
	private void createProfile(String profileDirectory, String name) {
		File targetFile = new File(profileDirectory + "/" + name);
		try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name)){
			Files.copy(inputStream, targetFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
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