package sikrip.roaddyno.web.model;

import sikrip.roaddyno.engine.AccelerationBounds;
import sikrip.roaddyno.engine.DynoRunDetector;
import sikrip.roaddyno.model.LogEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Contains the read log file data along with possible WOT runs within these data.
 */
public final class LogFileData {

	/**
	 * True for log files that are RPM based (usually ECU logs)
	 * false for speed based log files (usually gps log files).
	 */
	private final boolean rpmBased;

	/**
	 * The raw entries produced after reading a log file.
	 */
	private final List<LogEntry> logEntries;

	/**
	 * The bounds(indeces within the {@link #logEntries}) of the possible WOT runs.
	 */
	private final List<WOTRunBounds> WOTRunBoundses = new ArrayList<>();

	private final Set<String> auxiliaryPlotFields;

	public LogFileData(boolean rpmBased, List<LogEntry> logEntries, String... auxiliaryPlotFields) {
		this.rpmBased = rpmBased;
		this.logEntries = logEntries;
		this.WOTRunBoundses.addAll(findAccelerationRuns(rpmBased, logEntries));
		if (auxiliaryPlotFields != null) {
			this.auxiliaryPlotFields = Arrays.stream(auxiliaryPlotFields).collect(Collectors.toSet());
		} else {
			this.auxiliaryPlotFields = null;
		}
	}

	/**
	 * Finds the possible WOT runs on the provided log entries.
	 *
	 * @param rpmBased
	 * 		true if the log entries are RPM based, false otherwise
	 * @param logEntries
	 * 		the raw data
	 * @return a list of possible WOT runs
	 */
	private List<WOTRunBounds> findAccelerationRuns(boolean rpmBased, List<LogEntry> logEntries) {
		final List<WOTRunBounds> wotRuns = new ArrayList<>();
		final List<AccelerationBounds> accelerations;
		if (rpmBased) {
			accelerations = DynoRunDetector.getAccelerationBoundsByRPM(logEntries);
		} else {
			accelerations = DynoRunDetector.getAccelerationBoundsBySpeed(logEntries);
		}
		for (AccelerationBounds accelerationBounds : accelerations) {
			final int start = accelerationBounds.getStart();
			final int end = accelerationBounds.getEnd();
			wotRuns.add(new WOTRunBounds(start, end, logEntries.get(start), logEntries.get(end)));
		}
		// sort by speed diff descending
		return wotRuns.stream().sorted((o1, o2) -> {
			if (o1.getVelocityDiff() == o2.getVelocityDiff()) {
				return 0;
			} else if (o2.getVelocityDiff() < o1.getVelocityDiff()) {
				return -1;
			} else {
				return 1;
			}
		}).collect(Collectors.toList());
	}

	public boolean isRpmBased() {
		return rpmBased;
	}

	public List<LogEntry> getLogEntries() {
		return logEntries;
	}

	public List<WOTRunBounds> getWOTRunBoundses() {
		return WOTRunBoundses;
	}

	public Set<String> getAuxiliaryPlotFields() {
		return auxiliaryPlotFields;
	}
}
