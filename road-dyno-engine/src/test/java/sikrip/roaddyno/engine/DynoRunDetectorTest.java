package sikrip.roaddyno.engine;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import sikrip.roaddyno.eculogreader.EcuLogReader;
import sikrip.roaddyno.eculogreader.MegasquirtLogReader;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.LogValue;

/**
 * Tests for {@link DynoRunDetector}.
 */
public class DynoRunDetectorTest {

	@Test
	@Ignore
	public void validateDynoRunStartAndFinish_noDeceleration() {

		List<LogEntry> logEntries = new ArrayList<>();

		for (int i = 0; i < 50; i++) {
			Map<String, LogValue<Double>> values = new HashMap<>();
			values.put("Time", new LogValue<>(100.0 + (i / 10.0), "sec"));
			values.put("RPM", new LogValue<>(3000.0, "RPM"));
			values.put("TPS", new LogValue<>(20.0, "%"));
			new LogEntry(values, "Time", "RPM", "TPS");

			logEntries.add(new LogEntry(values, "Time", "RPM", "TPS"));
		}

		for (double i = 0; i < 2000; i++) {
			Map<String, LogValue<Double>> values = new HashMap<>();
			values.put("Time", new LogValue<>(100.0 + (i / 10.0), "sec"));
			values.put("RPM", new LogValue<>(3000.0 + (i * 2), "RPM"));
			values.put("TPS", new LogValue<>(i < 4 ? 80.0 : 100.0, "%"));
			new LogEntry(values, "Time", "RPM", "TPS");

			logEntries.add(new LogEntry(values, "Time", "RPM", "TPS"));
		}

		DynoRunDetector.getAccelerationBounds(logEntries);
	}

	@Test
	public void verifySampleMSLFile() throws Exception {

		EcuLogReader logReader = new MegasquirtLogReader();
		//List<LogEntry> logEntries = logReader.readLog(getTestResourceUrl("/sample-dyno-run.msl").getPath(), 0);
		List<LogEntry> logEntries = logReader.readLog(getTestResourceUrl("/sample-dyno-run-1.msl").getPath(), 0);

		AccelerationBounds accelerationBounds = DynoRunDetector.getAccelerationBounds(logEntries).get(0	);

		System.out.println(accelerationBounds);
		System.out.println("Start: " + logEntries.get(accelerationBounds.getStart()));
		System.out.println("End: " + logEntries.get(accelerationBounds.getEnd()));
	}

	public static URL getTestResourceUrl(String filename) {
		URL resource = DynoRunDetectorTest.class.getResource(filename);
		if (resource == null) {
			throw new RuntimeException("Cannot find resource:" + filename);
		}
		return resource;
	}
}
