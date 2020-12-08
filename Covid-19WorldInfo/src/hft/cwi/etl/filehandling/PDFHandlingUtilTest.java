package hft.cwi.etl.filehandling;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.junit.jupiter.api.Test;

class PDFHandlingUtilTest {

	@Test
	void test() throws MalformedURLException, IOException{
		File file = new File("testResources/denn, weil und deshalb.pdf");
		assertEquals(PDFHandlingUtil.getPDFFileContent(file),"denn, weil und deshalb\r\n"
				+ "Wiederholung\r\n"
				+ "weil -> Grund\r\n"
				+ "denn -> Grund\r\n"
				+ "deshalb -> Folge\r\n"
				+ "Ich bleibe zu Hause, weil ich krank bin.\r\n"
				+ "Ich bleibe zu Hause, denn ich bin krank.\r\n"
				+ "Ich bin krank, deshalb bleibe ich zu Hause.\r\n"
				+ "Satzbau\r\n"
				+ "I II 0 I II\r\n"
				+ "Ich esse eine Pizza, denn ich habe Hunger.\r\n"
				+ "Ich esse eine Pizza, weil ich Hunger habe.\r\n"
				+ "Ich habe Hunger, deshalb esse ich eine Pizza.\r\n");
	}

}
