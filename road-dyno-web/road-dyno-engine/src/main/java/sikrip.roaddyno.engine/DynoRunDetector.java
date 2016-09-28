package sikrip.roaddyno.engine;

import java.util.ArrayList;
import java.util.List;

import sikrip.roaddyno.model.LogEntry;

/**
 * Responsible to detect the start and the finish of a dyno run.
 */
public final class DynoRunDetector {

	/**
	 * Require at least this amount of speed diff to consider a WOT acceleration valid.
	 */
	private static final double VELOCITY_DIFF_THRESHOLD = 10; // km/h

	private DynoRunDetector() {
	}

	public static List<AccelerationBounds> getAccelerationBoundsByRPM(List<LogEntry> logEntries) {
		//TODO find all acceleration runs (currently only the first is found)
		AccelerationBounds firstAccelerationBounds = LogValuesUtilities.getAccelerationBoundsByRPM(logEntries);
		List<AccelerationBounds> accelerationBounds = new ArrayList<>();
		accelerationBounds.add(firstAccelerationBounds);
		return accelerationBounds;
	}

	public static List<AccelerationBounds> getAccelerationBoundsBySpeed(List<LogEntry> logEntries) {
		int offset = 0;
		final List<AccelerationBounds> accelerationBoundsList = new ArrayList<>();
		while (offset < logEntries.size() - 2) {

			AccelerationBounds accelerationBounds = LogValuesUtilities.getAccelerationBoundsBySpeed(logEntries, offset);
			if (accelerationBounds == null) {
				// no more accelerations
				break;
			}

			if (accelerationBounds.getEnd() < offset) {
				throw new IllegalStateException("Error detecting WOT runs on log file.");
			}

			final double startVelocity = logEntries.get(accelerationBounds.getStart()).getVelocity().getValue();
			final double endVelocity = logEntries.get(accelerationBounds.getEnd()).getVelocity().getValue();
			if (endVelocity - startVelocity > VELOCITY_DIFF_THRESHOLD) {
				accelerationBoundsList.add(accelerationBounds);
			}
			offset = accelerationBounds.getEnd();
		}
		return accelerationBoundsList;
	}

}
