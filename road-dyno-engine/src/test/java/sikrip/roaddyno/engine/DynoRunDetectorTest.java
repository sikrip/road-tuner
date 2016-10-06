package sikrip.roaddyno.engine;

import static junit.framework.Assert.assertEquals;

import java.net.URL;
import java.util.List;

import org.junit.Test;

import sikrip.roaddyno.logreader.MegasquirtLogReader;
import sikrip.roaddyno.logreader.VBOLogReader;
import sikrip.roaddyno.model.LogEntry;

/**
 * Tests for {@link DynoRunDetector}.
 */
public class DynoRunDetectorTest {

	@Test
	public void verifySampleMSLFiles() throws Exception {

		MegasquirtLogReader logReader = new MegasquirtLogReader();

		List<LogEntry> logEntries = logReader.readLog(getTestResourceUrl("/sample-dyno-run.msl").getPath());
		List<AccelerationBounds> accelerationBounds = DynoRunDetector.getAccelerationBoundsByRPM(logEntries);

		assertEquals(1, accelerationBounds.size());
		assertEquals(464, accelerationBounds.get(0).getStart());
		assertEquals(602, accelerationBounds.get(0).getEnd());
	}

	@Test
	public void verifySampleVBOFile_SingleAcceleration() throws Exception {
		VBOLogReader logReader = new VBOLogReader();

		List<LogEntry> logEntries = logReader.readLog(getTestResourceUrl("/sample-vbo.vbo").getPath());
		AccelerationBounds accelerationBounds = DynoRunDetector.getAccelerationBoundsBySpeed(logEntries).get(0);

		System.out.println("sample-vbo.vbo " + accelerationBounds);
		System.out.println(logEntries.get(accelerationBounds.getStart()) + " => " + logEntries.get(accelerationBounds.getEnd()));

		assertEquals(0, accelerationBounds.getStart());
		assertEquals(48, accelerationBounds.getEnd());
	}

	@Test
	public void verifySampleVBOFile_MultipleAccelerations() throws Exception {
		VBOLogReader logReader = new VBOLogReader();

		List<LogEntry> logEntries = logReader.readLog(getTestResourceUrl("/sample-vbo-1.vbo").getPath());

		System.out.println("sample-vbo.vbo");
		for (AccelerationBounds accelerationBounds : DynoRunDetector.getAccelerationBoundsBySpeed(logEntries)) {
			System.out.println("" + accelerationBounds.getStart() + ": " + logEntries.get(accelerationBounds.getStart()).getVelocity()
					+ " => " + accelerationBounds.getEnd()+": "+ logEntries.get(accelerationBounds.getEnd()).getVelocity());

		}
	}

	@Test
	public void verifyVBOWithNoDecelAtTheEnd() throws Exception {
		VBOLogReader logReader = new VBOLogReader();

		List<LogEntry> logEntries = logReader.readLog(getTestResourceUrl("/vbo-no-deceleration-at-the-end.vbo").getPath());

		System.out.println("vbo-no-deceleration-at-the-end.vbo");
		for (AccelerationBounds accelerationBounds : DynoRunDetector.getAccelerationBoundsBySpeed(logEntries)) {
			System.out.println("" + accelerationBounds.getStart() + ": " + logEntries.get(accelerationBounds.getStart()).getVelocity()
					+ " => " + accelerationBounds.getEnd()+": "+ logEntries.get(accelerationBounds.getEnd()).getVelocity());

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
