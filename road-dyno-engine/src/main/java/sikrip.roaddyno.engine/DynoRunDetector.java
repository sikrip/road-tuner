package sikrip.roaddyno.engine;

import java.util.ArrayList;
import java.util.List;

import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.WotRunBounds;

/**
 * Responsible to detect the start and the finish of a dyno run.
 */
public final class DynoRunDetector {

	/**
	 * Require at least this amount of speed diff to consider a WOT acceleration valid.
	 * Used on speed based dyno simulations.
	 */
	private static final double SPEED_WOT_DIFF_THRESHOLD = 26; // km/h

	/**
	 * Maximum Number of deceleration log entries in order to decide that deceleration is occurring.
	 * Used on speed based dyno simulations.
	 */
	private static final int DECEL_COUNT_THRESHOLD = 3;

	/**
	 * Require at least this amount of RPM diff to consider a WOT acceleration valid.
	 * Used on RPM based dyno simulations.
	 */
	private static final double RPM_WOT_DIFF_THRESHOLD = 2000;

	/**
	 * Consider WOT is happening when TPS is at this percentage at least.
	 * Used on RPM based dyno simulations.
	 */
	private static final double TPS_WOT_THRESHOLD = 0.96;

	private DynoRunDetector() {
		// prevent instantiation
	}

	/**
	 * Detects all the WOT runs in the given collection of log entries.
	 * Uses the RPM values for this detection.
	 *
	 * @param logEntries the log entries to check
	 * @return a collection of WOT bounds (start / end indices)
	 */
	public static List<WotRunBounds> getWotRunBoundsByRPM(List<LogEntry> logEntries) {
		int offset = 0;
		final List<WotRunBounds> wotRunBoundsList = new ArrayList<>();
		while (offset < logEntries.size() - 2) {

			WotRunBounds wotRunBounds = LogValuesUtilities.getWotBoundsByRPM(TPS_WOT_THRESHOLD, logEntries, offset);
			if (wotRunBounds == null) {
				// no more accelerations
				break;
			}

			if (wotRunBounds.getEnd() < offset) {
				throw new IllegalStateException("Error detecting WOT runs on log file.");
			}

			final double startRPM = logEntries.get(wotRunBounds.getStart()).getVelocity().getValue();
			final double endRPM = logEntries.get(wotRunBounds.getEnd()).getVelocity().getValue();
			if (endRPM - startRPM > RPM_WOT_DIFF_THRESHOLD) {
				wotRunBoundsList.add(wotRunBounds);
			}
			offset = wotRunBounds.getEnd();
		}
		return wotRunBoundsList;
	}

	/**
	 * Detects all the WOT runs in the given collection of log entries.
	 * Uses the speed values for this detection.
	 *
	 * @param logEntries the log entries to check
	 * @return a collection of WOT bounds (start / end indices)
	 */
	public static List<WotRunBounds> getWotRunBoundsBySpeed(List<LogEntry> logEntries) {
		int offset = 0;
		final List<WotRunBounds> wotRunBoundsList = new ArrayList<>();
		while (offset < logEntries.size() - 2) {

			WotRunBounds wotRunBounds = LogValuesUtilities.getWotBoundsBySpeed(DECEL_COUNT_THRESHOLD, logEntries, offset);
			if (wotRunBounds == null) {
				// no more accelerations
				break;
			}

			if (wotRunBounds.getEnd() < offset) {
				throw new IllegalStateException("Error detecting WOT runs on log file.");
			}

			final double startVelocity = logEntries.get(wotRunBounds.getStart()).getVelocity().getValue();
			final double endVelocity = logEntries.get(wotRunBounds.getEnd()).getVelocity().getValue();
			if (endVelocity - startVelocity > SPEED_WOT_DIFF_THRESHOLD) {
				wotRunBoundsList.add(wotRunBounds);
			}
			offset = wotRunBounds.getEnd();
		}
		return wotRunBoundsList;
	}
}
