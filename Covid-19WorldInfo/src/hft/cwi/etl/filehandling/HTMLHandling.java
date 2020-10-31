package hft.cwi.etl.filehandling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HTMLHandling {

	public static String getRawHTMLData(InputStream inputStream) {
		StringBuilder builder = new StringBuilder();
		buildRawHTMLData(builder, inputStream);
		return builder.toString();
	}

	private static void buildRawHTMLData(StringBuilder builder, InputStream inputStream) {
		String inputLine;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))){
			while ((inputLine = br.readLine()) != null) {
				builder.append(inputLine + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
