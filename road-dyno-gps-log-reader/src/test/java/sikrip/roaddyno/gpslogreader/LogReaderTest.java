package sikrip.roaddyno.gpslogreader;

import static junit.framework.TestCase.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import sikrip.roaddyno.model.LogEntry;

public class LogReaderTest {


	@Test
	public void verifyValidLogReading() throws IOException {
		LogReader reader = new LogReader();

		List<LogEntry> logEntries = reader.readLog(getTestResourceUrl("/sample.vbo").getPath());

		assertEquals(4420, logEntries.size());
	}


	public static URL getTestResourceUrl(String filename) {
		URL resource = LogReaderTest.class.getResource(filename);
		if (resource == null) {
			throw new RuntimeException("Cannot find resource:" + filename);
		}
		return resource;
	}
}
