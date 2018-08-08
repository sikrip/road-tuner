package sikrip.roaddyno.engine;

import static junit.framework.Assert.assertEquals;

import java.net.URL;
import java.util.List;

import org.junit.Test;

import sikrip.roaddyno.logreader.MegasquirtLogReader;
import sikrip.roaddyno.logreader.VBOLogReader;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.WotRunBounds;

/**
 * Tests for {@link WotRunDetector}.
 */
public class WotRunDetectorTest {

	@Test
	public void verifySampleMSLFiles() throws Exception {

		MegasquirtLogReader logReader = new MegasquirtLogReader();

		List<LogEntry> logEntries = logReader.readLog(getTestResourceUrl("/sample-dyno-run.msl").getPath());
		List<WotRunBounds> wotRunBounds = WotRunDetector.getWotRunBounds(true, logEntries);

		assertEquals(1, wotRunBounds.size());
		assertEquals(464, wotRunBounds.get(0).getStart());
		assertEquals(602, wotRunBounds.get(0).getEnd());
	}

	@Test
	public void verifySampleVBOFile_SingleAcceleration() throws Exception {
		VBOLogReader logReader = new VBOLogReader();

		List<LogEntry> logEntries = logReader.readLog(getTestResourceUrl("/sample-vbo.vbo").getPath());
		WotRunBounds wotRunBounds = WotRunDetector.getWotRunBounds(false, logEntries).get(0);

		System.out.println("sample-vbo.vbo " + wotRunBounds);
		System.out.println(logEntries.get(wotRunBounds.getStart()) + " => " + logEntries.get(wotRunBounds.getEnd()));

		assertEquals(0, wotRunBounds.getStart());
		assertEquals(48, wotRunBounds.getEnd());
	}

	@Test
	public void verifySampleVBOFile_MultipleAccelerations() throws Exception {
		VBOLogReader logReader = new VBOLogReader();

		List<LogEntry> logEntries = logReader.readLog(getTestResourceUrl("/sample-vbo-1.vbo").getPath());

		System.out.println("sample-vbo.vbo");
		for (WotRunBounds wotRunBounds : WotRunDetector.getWotRunBounds(false, logEntries)) {
			System.out.println("" + wotRunBounds.getStart() + ": " + logEntries.get(wotRunBounds.getStart()).getVelocity()
					+ " => " + wotRunBounds.getEnd()+": "+ logEntries.get(wotRunBounds.getEnd()).getVelocity());

		}
	}

	@Test
	public void verifyVBOWithNoDecelAtTheEnd() throws Exception {
		VBOLogReader logReader = new VBOLogReader();

		List<LogEntry> logEntries = logReader.readLog(getTestResourceUrl("/vbo-no-deceleration-at-the-end.vbo").getPath());

		System.out.println("vbo-no-deceleration-at-the-end.vbo");
		for (WotRunBounds wotRunBounds : WotRunDetector.getWotRunBounds(false, logEntries)) {
			System.out.println("" + wotRunBounds.getStart() + ": " + logEntries.get(wotRunBounds.getStart()).getVelocity()
					+ " => " + wotRunBounds.getEnd()+": "+ logEntries.get(wotRunBounds.getEnd()).getVelocity());

		}
	}

	public static URL getTestResourceUrl(String filename) {
		URL resource = WotRunDetectorTest.class.getResource(filename);
		if (resource == null) {
			throw new RuntimeException("Cannot find resource:" + filename);
		}
		return resource;
	}
}
