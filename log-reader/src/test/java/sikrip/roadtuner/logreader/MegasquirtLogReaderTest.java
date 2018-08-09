package sikrip.roadtuner.logreader;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import sikrip.roadtuner.model.InvalidLogFileException;
import sikrip.roadtuner.model.LogEntry;

/**
 * Unit tests for {@link MegasquirtLogReader}.
 */
public class MegasquirtLogReaderTest {

	@Test(expected = InvalidLogFileException.class)
	public void emptyFileShouldProduceInvalidLogFormatException() throws InvalidLogFileException {

		MegasquirtLogReader reader = new MegasquirtLogReader();
		try {
			reader.readLog(getTestResourceUrl("/empty-file-log.msl").getPath());
		} catch (IOException e) {
			fail("Should fail with IO exception");
		}

	}

	@Test(expected = InvalidLogFileException.class)
	public void logWithNoEntriesShouldProduceInvalidLogFormatException() throws InvalidLogFileException {

		MegasquirtLogReader reader = new MegasquirtLogReader();
		try {
			reader.readLog(getTestResourceUrl("/no-values-log.msl").getPath());
		} catch (IOException e) {
			fail("Should fail with IO exception");
		}

	}

	@Test(expected = InvalidLogFileException.class)
	public void logWithNoTimeHeaderShouldProduceInvalidLogFormatException() throws InvalidLogFileException {

		MegasquirtLogReader reader = new MegasquirtLogReader();
		try {
			reader.readLog(getTestResourceUrl("/no-time-header-log.msl").getPath());
		} catch (IOException e) {
			fail("Should fail with IO exception");
		}

	}

	@Test(expected = InvalidLogFileException.class)
	public void logWithNoRPMHeaderShouldProduceInvalidLogFormatException() throws InvalidLogFileException {

		MegasquirtLogReader reader = new MegasquirtLogReader();
		try {
			reader.readLog(getTestResourceUrl("/no-rpm-header-log.msl").getPath());
		} catch (IOException e) {
			fail("Should fail with IO exception");
		}

	}

	@Test
	public void verifyValidLogReading() throws InvalidLogFileException {

		MegasquirtLogReader reader = new MegasquirtLogReader();
		try {
			List<LogEntry> logEntries = reader.readLog(getTestResourceUrl("/valid-log.msl").getPath());

			assertEquals(1406, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			assertNotNull(logEntry.getTime());
			assertEquals("s", logEntry.getTime().getUnit());
			assertNotNull(logEntry.getTime().getValue());

			assertNotNull(logEntry.getVelocity());
			assertEquals("RPM", logEntry.getVelocity().getUnit());
			assertNotNull(logEntry.getVelocity().getValue());

			assertNotNull(logEntry.getTps());
			assertEquals("%", logEntry.getTps().getUnit());
			assertNotNull(logEntry.getTps().getValue());

			logEntries = reader.readLog(getTestResourceUrl("/valid-log-without-comment-lines.msl").getPath());

			assertEquals(1406, logEntries.size());

			logEntry = logEntries.get(0);

			assertNotNull(logEntry.getTime());
			assertEquals("s", logEntry.getTime().getUnit());
			assertNotNull(logEntry.getTime().getValue());

			assertNotNull(logEntry.getVelocity());
			assertEquals("RPM", logEntry.getVelocity().getUnit());
			assertNotNull(logEntry.getVelocity().getValue());

			assertNotNull(logEntry.get("TPS"));
			assertEquals("%", logEntry.get("TPS").getUnit());
			assertNotNull(logEntry.get("TPS").getValue());

		} catch (IOException | InvalidLogFileException e) {
			fail("This should not fail");
		}

	}

	public static URL getTestResourceUrl(String filename) {
		URL resource = MegasquirtLogReaderTest.class.getResource(filename);
		if (resource == null) {
			throw new RuntimeException("Cannot find resource:" + filename);
		}
		return resource;
	}
}
