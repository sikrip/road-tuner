package sikrip.roaddyno.engine;



import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import sikrip.roaddyno.model.DynoSimulationEntry;
import sikrip.roaddyno.model.DynoSimulationResult;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.LogValue;

/**
 * Tests for {@link DynoSimulationResult}.
 */
public class DynoSimulationResultTest {

	@Test
	public void validateMaxValues() {
		List<DynoSimulationEntry> results = new ArrayList<>();
		List<LogEntry> logEntries = new ArrayList<>();
		for (double i = 1; i < 10; i++) {
			double rpm = 2000.0 + i * 700;

			Map<String, LogValue<Double>> values = new HashMap<>();
			values.put("time", new LogValue<>(i, "kmh"));
			values.put("rpm", new LogValue<>(rpm, "rpm"));

			logEntries.add(new LogEntry(values, "time", "rpm"));
			double power = 10.0 * i;
			if(i==9.0){
				power-=15;
			}
			results.add(new DynoSimulationEntry(rpm, power));
		}
		DynoSimulationResult result = new DynoSimulationResult(true, logEntries, results);

		assertEquals(80.0, result.maxPower().getPower());
		assertEquals(7600.0, result.maxPower().getRpm());
		assertEquals(80.0 * 5252 / 7600, result.maxTorque().getTorque());
	}

	@Test
	public void validateDataHeaders() {
		List<DynoSimulationEntry> results = new ArrayList<>();
		List<LogEntry> logEntries = new ArrayList<>();
		for (double i = 1; i < 10; i++) {
			double rpm = 2000.0 + i * 700;

			Map<String, LogValue<Double>> values = new HashMap<>();
			values.put("time", new LogValue<>(i, "kmh"));
			values.put("rpm", new LogValue<>(rpm, "rpm"));

			logEntries.add(new LogEntry(values, "time", "rpm"));
			double power = 10;
			results.add(new DynoSimulationEntry(rpm, power));
		}
		DynoSimulationResult result = new DynoSimulationResult(true, logEntries, results);

		assertEquals(2, result.getDataHeaders().size());
		assertTrue(result.getDataHeaders().contains("time"));
		assertTrue(result.getDataHeaders().contains("rpm"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void dynoResultWithEmptyOrNullLogEntriesShouldNotBeAllowed() {
		List<DynoSimulationEntry> results = new ArrayList<>();
		for (double i = 1; i < 10; i++) {
			double rpm = 2000.0 + i * 700;
			double power = 10;
			results.add(new DynoSimulationEntry(rpm, power));
		}
		new DynoSimulationResult(true, null, results);
	}

}
