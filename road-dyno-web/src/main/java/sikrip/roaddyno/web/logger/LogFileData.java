package sikrip.roaddyno.web.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sikrip.roaddyno.engine.AccelerationBounds;
import sikrip.roaddyno.engine.DynoRunDetector;
import sikrip.roaddyno.model.LogEntry;

public final class LogFileData {

	private final boolean rpmBased;
	private final List<LogEntry> logEntries;
	private final List<AccelerationRun> accelerationRuns = new ArrayList<>();

	public LogFileData(boolean rpmBased, List<LogEntry> logEntries) {
		this.rpmBased = rpmBased;
		this.logEntries = logEntries;
		this.accelerationRuns.addAll(createAccelerationRuns(rpmBased, logEntries));
	}

	public boolean isRpmBased() {
		return rpmBased;
	}

	public List<LogEntry> getLogEntries() {
		return logEntries;
	}

	public List<AccelerationRun> getAccelerationRuns() {
		return accelerationRuns;
	}

	private List<AccelerationRun> createAccelerationRuns(boolean rpmBased, List<LogEntry> logEntries) {
		final List<AccelerationRun> accelerationRuns = new ArrayList<>();
		if (rpmBased) {
			// FIXME for rpm based we do no acceleration detection for now
			accelerationRuns.add(new AccelerationRun(0, logEntries.size(), logEntries.get(0), logEntries.get(logEntries.size())));
		} else {
			for (AccelerationBounds accelerationBounds : DynoRunDetector.getAccelerationBoundsBySpeed(logEntries)) {
				final int start = accelerationBounds.getStart();
				final int end = accelerationBounds.getEnd();
				accelerationRuns.add(new AccelerationRun(start, end, logEntries.get(start), logEntries.get(end)));
			}
		}
		// sort by speed diff descending
		return accelerationRuns.stream().sorted((o1, o2) -> {
			if (o1.getVelocityDiff() == o2.getVelocityDiff()) {
				return 0;
			} else if (o2.getVelocityDiff() < o1.getVelocityDiff()) {
				return -1;
			} else {
				return 1;
			}
		}).collect(Collectors.toList());
	}
}
