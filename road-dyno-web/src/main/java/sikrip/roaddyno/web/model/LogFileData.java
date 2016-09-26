package sikrip.roaddyno.web.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sikrip.roaddyno.engine.AccelerationBounds;
import sikrip.roaddyno.engine.DynoRunDetector;
import sikrip.roaddyno.model.LogEntry;

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
	private final List<WOTRun> WOTRuns = new ArrayList<>();

	public LogFileData(boolean rpmBased, List<LogEntry> logEntries) {
		this.rpmBased = rpmBased;
		this.logEntries = logEntries;
		this.WOTRuns.addAll(findAccelerationRuns(rpmBased, logEntries));
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
	private List<WOTRun> findAccelerationRuns(boolean rpmBased, List<LogEntry> logEntries) {
		final List<WOTRun> WOTRuns = new ArrayList<>();
		if (rpmBased) {
			// FIXME for rpm based we do no acceleration detection for now
			WOTRuns.add(new WOTRun(0, logEntries.size(), logEntries.get(0), logEntries.get(logEntries.size() - 1)));
		} else {
			for (AccelerationBounds accelerationBounds : DynoRunDetector.getAccelerationBoundsBySpeed(logEntries)) {
				final int start = accelerationBounds.getStart();
				final int end = accelerationBounds.getEnd();
				WOTRuns.add(new WOTRun(start, end, logEntries.get(start), logEntries.get(end)));
			}
		}
		// sort by speed diff descending
		return WOTRuns.stream().sorted((o1, o2) -> {
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

	public List<WOTRun> getWOTRuns() {
		return WOTRuns;
	}
}
