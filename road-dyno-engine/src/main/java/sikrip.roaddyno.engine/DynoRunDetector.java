package sikrip.roaddyno.engine;

import java.util.ArrayList;
import java.util.List;

import sikrip.roaddyno.model.LogEntry;

/**
 * Responsible to detect the start and the finish of a dyno run.
 */
final class DynoRunDetector {

	private DynoRunDetector() {
	}

	static List<AccelerationBounds> getAccelerationBoundsByRPM(List<LogEntry> logEntries) {
		//TODO find all acceleration runs (currently only the first is found)
		AccelerationBounds firstAccelerationBounds = LogValuesUtilities.getAccelerationBoundsByRPM(logEntries);
		List<AccelerationBounds> accelerationBounds = new ArrayList<>();
		accelerationBounds.add(firstAccelerationBounds);
		return accelerationBounds;
	}

	static List<AccelerationBounds> getAccelerationBoundsBySpeed(List<LogEntry> logEntries) {
		int offset = 0;
		final List<AccelerationBounds> accelerationBoundsList = new ArrayList<>();
		while (offset < logEntries.size() - 2) {
			AccelerationBounds accelerationBounds = LogValuesUtilities.getAccelerationBoundsBySpeed(logEntries, offset);
			if (accelerationBounds == null) {
				// no more accelerations
				break;
			}
			accelerationBoundsList.add(accelerationBounds);
			offset += accelerationBounds.getEnd();
		}

		return accelerationBoundsList;
	}

}
