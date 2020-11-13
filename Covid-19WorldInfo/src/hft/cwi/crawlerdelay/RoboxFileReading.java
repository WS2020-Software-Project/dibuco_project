package hft.cwi.crawlerdelay;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;


public class RoboxFileReading {

	public static void main(String[] args) throws FileNotFoundException, IOException {
	//	InputStreamReader input;
		Properties properties = new Properties();
		properties.load(new FileInputStream("resources/crawler.properties"));		
	    try(BufferedReader in = new BufferedReader(new InputStreamReader(new URL(properties.getProperty("crawler.robot.who")).openStream()))) {
	        String line = null;
	        while((line = in.readLine()) != null) {
	            System.out.println(line);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

}
