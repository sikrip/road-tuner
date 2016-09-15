package sikrip.roaddyno.eculogreader;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import sikrip.roaddyno.model.LogEntry;

/**
 * Unit tests for {@link MegasquirtLogReader}.
 */
public class MegasquirtLogReaderTest {

	@Test(expected = InvalidLogFormatException.class)
	public void emptyFileShouldProduceInvalidLogFormatException() throws InvalidLogFormatException {

		EcuLogReader reader = new MegasquirtLogReader();
		try {
			reader.readLog(getTestResourceUrl("/empty-file-log.msl").getPath(), 98);
		} catch (IOException e) {
			fail("Should fail with IO exception");
		}

	}

	@Test(expected = InvalidLogFormatException.class)
	public void logWithNoEntriesShouldProduceInvalidLogFormatException() throws InvalidLogFormatException {

		EcuLogReader reader = new MegasquirtLogReader();
		try {
			reader.readLog(getTestResourceUrl("/no-values-log.msl").getPath(), 98);
		} catch (IOException e) {
			fail("Should fail with IO exception");
		}

	}

	@Test(expected = InvalidLogFormatException.class)
	public void logWithNoTimeHeaderShouldProduceInvalidLogFormatException() throws InvalidLogFormatException {

		EcuLogReader reader = new MegasquirtLogReader();
		try {
			reader.readLog(getTestResourceUrl("/no-time-header-log.msl").getPath(), 98);
		} catch (IOException e) {
			fail("Should fail with IO exception");
		}

	}

	@Test(expected = InvalidLogFormatException.class)
	public void logWithNoRPMHeaderShouldProduceInvalidLogFormatException() throws InvalidLogFormatException {

		EcuLogReader reader = new MegasquirtLogReader();
		try {
			reader.readLog(getTestResourceUrl("/no-rpm-header-log.msl").getPath(), 98);
		} catch (IOException e) {
			fail("Should fail with IO exception");
		}

	}

	@Test
	public void verifyValidLogReading() throws InvalidLogFormatException {

		EcuLogReader reader = new MegasquirtLogReader();
		try {
			List<LogEntry> logEntries = reader.readLog(getTestResourceUrl("/valid-log.msl").getPath(), 98);

			assertEquals(117, logEntries.size());

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

			logEntries = reader.readLog(getTestResourceUrl("/valid-log-without-comment-lines.msl").getPath(), 98);

			assertEquals(117, logEntries.size());

			logEntry = logEntries.get(0);

			assertNotNull(logEntry.getTime());
			assertEquals("s", logEntry.getTime().getUnit());
			assertNotNull(logEntry.getTime().getValue());

			assertNotNull(logEntry.getVelocity());
			assertEquals("RPM", logEntry.getVelocity().getUnit());
			assertNotNull(logEntry.getVelocity().getValue());

			assertNotNull(logEntry.getTps());
			assertEquals("%", logEntry.getTps().getUnit());
			assertNotNull(logEntry.getTps().getValue());

		} catch (IOException | InvalidLogFormatException e) {
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
