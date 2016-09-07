package sikrip.roaddyno.engine;

import java.util.List;

import sikrip.roaddyno.model.LogEntry;

/**
 * Responsible to detect the start and the finish of a dyno run.
 */
final class DynoRunDetector {

	private final static int NUMBER_OF_ACCELERATION_ENTRIES = 30;
	private final static int NUMBER_OF_DECELERATION_ENTRIES = 10;

	private DynoRunDetector() {
	}

	static int[] getDynoRunBoundaries(List<LogEntry> logEntries, int startIdx) {

		if (logEntries.size() < NUMBER_OF_ACCELERATION_ENTRIES + NUMBER_OF_DECELERATION_ENTRIES + 1) {
			throw new IllegalArgumentException("Not enough log entries.");
		}

		Double velocity = logEntries.get(startIdx).getRpm().getValue();
		int iLogEntry = startIdx + 1;

		int accelerationCount = 0;
		for (; iLogEntry < logEntries.size(); iLogEntry++) {
			if (logEntries.get(iLogEntry).getRpm().getValue() > velocity) {
				accelerationCount++;
			}
			velocity = logEntries.get(iLogEntry).getRpm().getValue();
			if (accelerationCount == NUMBER_OF_ACCELERATION_ENTRIES) {
				break;
			}
		}
		if (iLogEntry == logEntries.size()) {
			throw new IllegalArgumentException("No acceleration found in the provided log entries");
		}
		int start = iLogEntry;

		velocity = logEntries.get(start).getRpm().getValue();
		int decelerationCount = 0;
		for (; iLogEntry < logEntries.size(); iLogEntry++) {
			if (logEntries.get(iLogEntry).getRpm().getValue() < velocity) {
				decelerationCount++;
			}
			if (decelerationCount == NUMBER_OF_DECELERATION_ENTRIES) {
				break;
			}
		}
		int finish;
		if (iLogEntry == logEntries.size()) {
			// no deceleration detected, assuming run finish to the end to the log
			finish = iLogEntry - 1;
		} else {
			finish = iLogEntry;
		}

		return new int[] { start, finish };
	}
}
