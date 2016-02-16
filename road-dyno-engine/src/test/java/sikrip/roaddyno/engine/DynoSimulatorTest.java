package sikrip.roaddyno.engine;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import sikrip.roaddyno.model.LogEntry;

/**
 * Unit tests for {@link DynoSimulator}
 */
public class DynoSimulatorTest {

	@Test(expected = InvalidSimulationParameterException.class)
	public void ifTooFewEntriesProvided_ExceptionShouldBeThrown() throws InvalidSimulationParameterException {
		List<LogEntry> tooFewEntries = new ArrayList<>();
		DynoSimulator.run(tooFewEntries, 4.312, 1.310, 528, 905, 85, 1.7, 0.34);
	}

	@Test(expected = InvalidSimulationParameterException.class)
	public void onNonPositiveFGR_ExceptionShouldBeThrown() throws InvalidSimulationParameterException {
		List<LogEntry> entries = Mockito.mock(List.class);
		Mockito.when(entries.size()).thenReturn(100);
		DynoSimulator.run(entries, 0, 1.310, 528, 905, 85, 1.7, 0.34);
	}

	@Test(expected = InvalidSimulationParameterException.class)
	public void onNonPositiveGR_ExceptionShouldBeThrown() throws InvalidSimulationParameterException {
		List<LogEntry> entries = Mockito.mock(List.class);
		Mockito.when(entries.size()).thenReturn(100);
		DynoSimulator.run(entries, 4.312, -0.2, 528, 905, 85, 1.7, 0.34);
	}

	@Test(expected = InvalidSimulationParameterException.class)
	public void onNonPositiveTyreDiameter_ExceptionShouldBeThrown() throws InvalidSimulationParameterException {
		List<LogEntry> entries = Mockito.mock(List.class);
		Mockito.when(entries.size()).thenReturn(100);
		DynoSimulator.run(entries, 4.312, 1.310, -200, 905, 85, 1.7, 0.34);
	}

	@Test(expected = InvalidSimulationParameterException.class)
	public void onNonPositiveCarWeight_ExceptionShouldBeThrown() throws InvalidSimulationParameterException {
		List<LogEntry> entries = Mockito.mock(List.class);
		Mockito.when(entries.size()).thenReturn(100);
		DynoSimulator.run(entries, 4.312, 1.310, 528, -905, 85, 1.7, 0.34);
	}

	@Test(expected = InvalidSimulationParameterException.class)
	public void onNonPositiveOccupantsWeight_ExceptionShouldBeThrown() throws InvalidSimulationParameterException {
		List<LogEntry> entries = Mockito.mock(List.class);
		Mockito.when(entries.size()).thenReturn(100);
		DynoSimulator.run(entries, 4.312, 1.310, 528, 905, 0, 1.7, 0.34);
	}

	@Test(expected = InvalidSimulationParameterException.class)
	public void onNonPositiveFA_ExceptionShouldBeThrown() throws InvalidSimulationParameterException {
		List<LogEntry> entries = Mockito.mock(List.class);
		Mockito.when(entries.size()).thenReturn(100);
		DynoSimulator.run(entries, 4.312, 1.310, 528, 905, 85, -1.7, 0.34);
	}

	@Test(expected = InvalidSimulationParameterException.class)
	public void onNonPositiveCD_ExceptionShouldBeThrown() throws InvalidSimulationParameterException {
		List<LogEntry> entries = Mockito.mock(List.class);
		Mockito.when(entries.size()).thenReturn(100);
		DynoSimulator.run(entries, 4.312, 1.310, 528, 905, 85, 1.7, -0.34);
	}

}
