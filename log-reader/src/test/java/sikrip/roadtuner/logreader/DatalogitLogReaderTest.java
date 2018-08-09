package sikrip.roadtuner.logreader;

import org.junit.Test;
import sikrip.roadtuner.model.InvalidLogFileException;
import sikrip.roadtuner.model.LogEntry;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Unit tests for {@link DatalogitLogReader}.
 */
public class DatalogitLogReaderTest {

	@Test(expected = InvalidLogFileException.class)
	public void emptyFileShouldProduceInvalidLogFormatException() throws InvalidLogFileException {

		DatalogitLogReader reader = new DatalogitLogReader();
		try {
			reader.readLog(getTestResourceUrl("/empty-file-log.txt").getPath());
		} catch (IOException e) {
			fail("Should fail with IO exception");
		}

	}

	@Test(expected = InvalidLogFileException.class)
	public void logWithNoEntriesShouldProduceInvalidLogFormatException() throws InvalidLogFileException {

		DatalogitLogReader reader = new DatalogitLogReader();
		try {
			reader.readLog(getTestResourceUrl("/no-values-datalogit-log.txt").getPath());
		} catch (IOException e) {
			fail("Should fail with IO exception");
		}

	}

	@Test(expected = InvalidLogFileException.class)
	public void logWithNoTimeHeaderShouldProduceInvalidLogFormatException() throws InvalidLogFileException {

		DatalogitLogReader reader = new DatalogitLogReader();
		try {
			reader.readLog(getTestResourceUrl("/no-req-column-datalogit-log.txt").getPath());
		} catch (IOException e) {
			fail("Should fail with IO exception");
		}

	}

	@Test(expected = InvalidLogFileException.class)
	public void logWithNoRPMHeaderShouldProduceInvalidLogFormatException() throws InvalidLogFileException {

		DatalogitLogReader reader = new DatalogitLogReader();
		try {
			reader.readLog(getTestResourceUrl("/no-rpm-header-log.msl").getPath());
		} catch (IOException e) {
			fail("Should fail with IO exception");
		}

	}

	@Test
	public void verifyValidLogReading() throws InvalidLogFileException {

		DatalogitLogReader reader = new DatalogitLogReader();
		try {
			List<LogEntry> logEntries = reader.readLog(getTestResourceUrl("/valid-datalogit-log.txt").getPath());

			assertEquals(915, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			assertNotNull(logEntry.getTime());
			assertNotNull(logEntry.getVelocity());
			assertNotNull(logEntry.getTps());
		} catch (IOException | InvalidLogFileException e) {
			fail("This should not fail");
		}

	}

	private static URL getTestResourceUrl(String filename) {
		URL resource = DatalogitLogReaderTest.class.getResource(filename);
		if (resource == null) {
			throw new RuntimeException("Cannot find resource:" + filename);
		}
		return resource;
	}
}
