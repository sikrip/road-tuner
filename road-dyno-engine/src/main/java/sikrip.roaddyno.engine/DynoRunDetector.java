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

	static List<AccelerationBounds> getAccelerationBounds(List<LogEntry> logEntries) {
		//TODO find all acceleration runs (currently only the first is found)
		AccelerationBounds firstAccelerationBounds = RPMUtilities.getMaxAccelerationBounds(logEntries);
		List<AccelerationBounds> accelerationBounds = new ArrayList<>();
		accelerationBounds.add(firstAccelerationBounds);
		return accelerationBounds;
	}

}
