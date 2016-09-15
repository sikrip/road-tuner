package sikrip.roaddyno.engine;

import static junit.framework.TestCase.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

import sikrip.roaddyno.model.DynoSimulationResult;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.LogValue;

/**
 * Unit tests for {@link DynoSimulator}
 */
public class RPMDynoSimulatorTest {

	@Test
	public void validLogs_ValidSimulation() throws SimulationException {
		List<LogEntry> logEntries = new ArrayList<>();

		for (double i = 0; i < 2000; i++) {
			Map<String, LogValue<Double>> values = new HashMap<>();
			values.put("Time", new LogValue<>(100.0 + (i / 10.0), "sec"));
			values.put("RPM", new LogValue<>(3000.0 + (i * 2), "RPM"));
			values.put("TPS", new LogValue<>(i < 4 ? 40.0 : 100.0, "%"));
			new LogEntry(values, "Time", "RPM", "TPS");

			logEntries.add(new LogEntry(values, "Time", "RPM", "TPS"));
		}

		DynoSimulationResult run = DynoSimulator.runByRPM(logEntries, 4.312, 1.310, 528, 905, 85, 1.7, 0.34);

		assertEquals(1999, run.getEntriesSize());
	}

	@Test(expected = SimulationException.class)
	public void onNonIncreasingTimeValues_ExceptionShouldBeThrown() throws SimulationException {

		List<LogEntry> logEntries = new ArrayList<>();

		for (int i = 0; i < 20; i++) {
			Map<String, LogValue<Double>> values = new HashMap<>();
			values.put("Time", new LogValue<>(100.0, "sec"));
			values.put("RPM", new LogValue<>(3000.0, "RPM"));
			values.put("TPS", new LogValue<>(100.0, "%"));
			new LogEntry(values, "Time", "RPM", "TPS");

			logEntries.add(new LogEntry(values, "Time", "RPM", "TPS"));
		}

		DynoSimulationResult run = DynoSimulator.runByRPM(logEntries, 4.312, 1.310, 528, 905, 85, 1.7, 0.34);

		assertEquals(1999, run.getEntriesSize());
	}

	@Test(expected = SimulationException.class)
	public void ifTooFewEntriesProvided_ExceptionShouldBeThrown() throws SimulationException {
		List<LogEntry> tooFewEntries = new ArrayList<>();
		DynoSimulator.runByRPM(tooFewEntries, 4.312, 1.310, 528, 905, 85, 1.7, 0.34);
	}

	@Test(expected = SimulationException.class)
	public void onNonPositiveFGR_ExceptionShouldBeThrown() throws SimulationException {
		List<LogEntry> entries = Mockito.mock(List.class);
		Mockito.when(entries.size()).thenReturn(100);
		DynoSimulator.runByRPM(entries, 0, 1.310, 528, 905, 85, 1.7, 0.34);
	}

	@Test(expected = SimulationException.class)
	public void onNonPositiveGR_ExceptionShouldBeThrown() throws SimulationException {
		List<LogEntry> entries = Mockito.mock(List.class);
		Mockito.when(entries.size()).thenReturn(100);
		DynoSimulator.runByRPM(entries, 4.312, -0.2, 528, 905, 85, 1.7, 0.34);
	}

	@Test(expected = SimulationException.class)
	public void onNonPositiveTyreDiameter_ExceptionShouldBeThrown() throws SimulationException {
		List<LogEntry> entries = Mockito.mock(List.class);
		Mockito.when(entries.size()).thenReturn(100);
		DynoSimulator.runByRPM(entries, 4.312, 1.310, -200, 905, 85, 1.7, 0.34);
	}

	@Test(expected = SimulationException.class)
	public void onNonPositiveCarWeight_ExceptionShouldBeThrown() throws SimulationException {
		List<LogEntry> entries = Mockito.mock(List.class);
		Mockito.when(entries.size()).thenReturn(100);
		DynoSimulator.runByRPM(entries, 4.312, 1.310, 528, -905, 85, 1.7, 0.34);
	}

	@Test(expected = SimulationException.class)
	public void onNonPositiveOccupantsWeight_ExceptionShouldBeThrown() throws SimulationException {
		List<LogEntry> entries = Mockito.mock(List.class);
		Mockito.when(entries.size()).thenReturn(100);
		DynoSimulator.runByRPM(entries, 4.312, 1.310, 528, 905, 0, 1.7, 0.34);
	}

	@Test(expected = SimulationException.class)
	public void onNonPositiveFA_ExceptionShouldBeThrown() throws SimulationException {
		List<LogEntry> entries = Mockito.mock(List.class);
		Mockito.when(entries.size()).thenReturn(100);
		DynoSimulator.runByRPM(entries, 4.312, 1.310, 528, 905, 85, -1.7, 0.34);
	}

	@Test(expected = SimulationException.class)
	public void onNonPositiveCD_ExceptionShouldBeThrown() throws SimulationException {
		List<LogEntry> entries = Mockito.mock(List.class);
		Mockito.when(entries.size()).thenReturn(100);
		DynoSimulator.runByRPM(entries, 4.312, 1.310, 528, 905, 85, 1.7, -0.34);
	}

}
