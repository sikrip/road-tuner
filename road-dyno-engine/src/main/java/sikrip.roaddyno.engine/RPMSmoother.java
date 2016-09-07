package sikrip.roaddyno.engine;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;

import sikrip.roaddyno.model.LogEntry;

/**
 * Smooths the RPM values of the given log entries using the Local Regression Algorithm (Loess, Lowess).
 */
final class RPMSmoother {

	/**
	 * Smooths the RPM values of the given log entries using the Local Regression Algorithm (Loess, Lowess).
	 *
	 * @param rawEntries
	 * 		the raw log entries
	 * @return the log entries with the RPM values smoothed
	 */
	static List<LogEntry> smoothRPM(List<LogEntry> rawEntries) {

		List<LogEntry> smoothedEntries = new ArrayList<>();

		double[] timeValues = new double[rawEntries.size()];
		double[] rawRPMValues = new double[rawEntries.size()];

		for (int i = 0; i < rawEntries.size(); i++) {
			LogEntry logEntry = rawEntries.get(i);
			timeValues[i] = logEntry.getTime().getValue();
			rawRPMValues[i] = logEntry.getRpm().getValue();
		}

		double[] smoothedRPMValues = new LoessInterpolator().smooth(timeValues, rawRPMValues);

		for (int i = 0; i < rawEntries.size(); i++) {
			LogEntry logEntryCopy = rawEntries.get(i).getCopy();
			logEntryCopy.getRpm().setValue(smoothedRPMValues[i]);
			smoothedEntries.add(logEntryCopy);
		}

		return smoothedEntries;
	}
}
