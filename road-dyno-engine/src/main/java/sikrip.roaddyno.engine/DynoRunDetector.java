package sikrip.roaddyno.engine;

import java.util.List;

import sikrip.roaddyno.model.LogEntry;

/**
 * Responsible to detect the start and the finish of a dyno run.
 */
final class DynoRunDetector {

	private DynoRunDetector() {
	}

	static AccelerationBounds getAccelerationRuns(List<LogEntry> logEntries) {
		return RPMUtilities.getMaxAccelerationBounds(logEntries);
	}

}
