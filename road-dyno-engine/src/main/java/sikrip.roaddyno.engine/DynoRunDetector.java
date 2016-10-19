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
	private static final double SPEED_WOT_DIFF_THRESHOLD = 25; // km/h

	/**
	 * Require at least this amount of RPM diff to consider a WOT acceleration valid.
	 */
	private static final double RPM_WOT_DIFF_THRESHOLD = 2000;

	/**
	 * Maximum Number of deceleration log entries in order to decide that deceleration is occurring.
	 */
	private static final int DECEL_COUNT_THRESHOLD = 3;

	/**
	 * Consider WOT is happening when TPS is at this value.
	 */
	private static final int TPS_WOT_THRESHOLD = 96;

	private DynoRunDetector() {}

	public static List<AccelerationBounds> getAccelerationBoundsByRPM(List<LogEntry> logEntries) {
		int offset = 0;
		final List<AccelerationBounds> accelerationBoundsList = new ArrayList<>();
		while (offset < logEntries.size() - 2) {

			AccelerationBounds accelerationBounds = LogValuesUtilities.getAccelerationBoundsByRPM(TPS_WOT_THRESHOLD, logEntries, offset);
			if (accelerationBounds == null) {
				// no more accelerations
				break;
			}

			if (accelerationBounds.getEnd() < offset) {
				throw new IllegalStateException("Error detecting WOT runs on log file.");
			}

			final double startRPM = logEntries.get(accelerationBounds.getStart()).getVelocity().getValue();
			final double endRPM = logEntries.get(accelerationBounds.getEnd()).getVelocity().getValue();
			if (endRPM - startRPM > RPM_WOT_DIFF_THRESHOLD) {
				accelerationBoundsList.add(accelerationBounds);
			}
			offset = accelerationBounds.getEnd();
		}
		return accelerationBoundsList;
	}

	public static List<AccelerationBounds> getAccelerationBoundsBySpeed(List<LogEntry> logEntries) {
		int offset = 0;
		final List<AccelerationBounds> accelerationBoundsList = new ArrayList<>();
		while (offset < logEntries.size() - 2) {

			AccelerationBounds accelerationBounds = LogValuesUtilities.getAccelerationBoundsBySpeed(DECEL_COUNT_THRESHOLD, logEntries, offset);
			if (accelerationBounds == null) {
				// no more accelerations
				break;
			}

			if (accelerationBounds.getEnd() < offset) {
				throw new IllegalStateException("Error detecting WOT runs on log file.");
			}

			final double startVelocity = logEntries.get(accelerationBounds.getStart()).getVelocity().getValue();
			final double endVelocity = logEntries.get(accelerationBounds.getEnd()).getVelocity().getValue();
			if (endVelocity - startVelocity > SPEED_WOT_DIFF_THRESHOLD) {
				accelerationBoundsList.add(accelerationBounds);
			}
			offset = accelerationBounds.getEnd();
		}
		return accelerationBoundsList;
	}
}
