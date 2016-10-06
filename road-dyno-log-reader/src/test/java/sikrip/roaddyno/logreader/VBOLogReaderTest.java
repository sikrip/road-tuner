package sikrip.roaddyno.logreader;

import static junit.framework.TestCase.assertEquals;

import java.net.URL;
import java.util.List;

import org.junit.Test;

import sikrip.roaddyno.model.LogEntry;

public class VBOLogReaderTest {

	@Test
	public void verifyValidLogReading() throws Exception {
		VBOLogReader reader = new VBOLogReader();
		List<LogEntry> logEntries = reader.readLog(getTestResourceUrl("/sample.vbo").getPath());
		assertEquals(4420, logEntries.size());
	}

	public static URL getTestResourceUrl(String filename) {
		URL resource = VBOLogReaderTest.class.getResource(filename);
		if (resource == null) {
			throw new RuntimeException("Cannot find resource:" + filename);
		}
		return resource;
	}
}
