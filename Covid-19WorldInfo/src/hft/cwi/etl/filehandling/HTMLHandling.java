package hft.cwi.etl.filehandling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HTMLHandling {

	public static String getRawHTMLData(InputStream inputStream) {
		StringBuilder builder = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		buildRawHTMLData(builder, br);
		return builder.toString();

	}

	private static void buildRawHTMLData(StringBuilder builder, BufferedReader br) {
		String inputLine;
		try {
			while ((inputLine = br.readLine()) != null) {
				builder.append(inputLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
