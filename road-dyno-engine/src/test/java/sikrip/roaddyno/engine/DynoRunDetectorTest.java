package sikrip.roaddyno.engine;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.net.URL;
import java.util.List;

import org.junit.Test;

import sikrip.roaddyno.eculogreader.EcuLogReader;
import sikrip.roaddyno.eculogreader.MegasquirtLogReader;
import sikrip.roaddyno.gpslogreader.GPSLogReader;
import sikrip.roaddyno.gpslogreader.VBOLogReader;
import sikrip.roaddyno.model.LogEntry;

/**
 * Tests for {@link DynoRunDetector}.
 */
public class DynoRunDetectorTest {

	@Test
	public void verifySampleMSLFiles() throws Exception {

		EcuLogReader logReader = new MegasquirtLogReader();

		List<LogEntry> logEntries = logReader.readLog(getTestResourceUrl("/sample-dyno-run.msl").getPath(), 0);
		AccelerationBounds accelerationBounds = DynoRunDetector.getAccelerationBoundsByRPM(logEntries).get(0);

		System.out.println("sample-dyno-run.msl " + accelerationBounds);
		assertTrue(accelerationBounds.getStart() > 470);
		assertTrue(accelerationBounds.getStart() < 615);

		logEntries = logReader.readLog(getTestResourceUrl("/sample-dyno-run-1.msl").getPath(), 0);
		accelerationBounds = DynoRunDetector.getAccelerationBoundsByRPM(logEntries).get(0);

		System.out.println("sample-dyno-run-1.msl " + accelerationBounds);
		assertTrue(accelerationBounds.getStart() > 680);
		assertTrue(accelerationBounds.getStart() < 800);
	}

	@Test
	public void verifySampleVBOFile_SingleAcceleration() throws Exception {
		GPSLogReader logReader = new VBOLogReader();

		List<LogEntry> logEntries = logReader.readLog(getTestResourceUrl("/sample-vbo.vbo").getPath());
		AccelerationBounds accelerationBounds = DynoRunDetector.getAccelerationBoundsBySpeed(logEntries).get(0);

		System.out.println("sample-vbo.vbo " + accelerationBounds);
		System.out.println(logEntries.get(accelerationBounds.getStart()) + " => " + logEntries.get(accelerationBounds.getEnd()));

		assertEquals(1, accelerationBounds.getStart());
		assertEquals(46, accelerationBounds.getEnd());
	}

	@Test
	public void verifySampleVBOFile_MultipleAccelerations() throws Exception {
		GPSLogReader logReader = new VBOLogReader();

		List<LogEntry> logEntries = logReader.readLog(getTestResourceUrl("/sample-vbo-1.vbo").getPath());

		for (AccelerationBounds accelerationBounds : DynoRunDetector.getAccelerationBoundsBySpeed(logEntries)) {
			System.out.println("sample-vbo.vbo " + accelerationBounds);
			System.out.println(logEntries.get(accelerationBounds.getStart()) + " => " + logEntries.get(accelerationBounds.getEnd()));

		}
	}

	public static URL getTestResourceUrl(String filename) {
		URL resource = DynoRunDetectorTest.class.getResource(filename);
		if (resource == null) {
			throw new RuntimeException("Cannot find resource:" + filename);
		}
		return resource;
	}
}
