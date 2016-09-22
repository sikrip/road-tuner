package sikrip.roaddyno.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import sikrip.roaddyno.model.LogEntry;

/**
 * Responsible to detect the start and the finish of a dyno run.
 */
public final class DynoRunDetector {

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
			if (logEntries.get(accelerationBounds.getStart()).getVelocity().getValue() < logEntries.get(accelerationBounds.getEnd()).getVelocity().getValue
					()) {
				accelerationBoundsList.add(accelerationBounds);
			}
			offset = accelerationBounds.getEnd();
		}
		// sort by speed diff
		return accelerationBoundsList.stream().sorted(new Comparator<AccelerationBounds>() {
			@Override
			public int compare(AccelerationBounds o1, AccelerationBounds o2) {
				if (o1.getVelocityDiff() == o2.getVelocityDiff()) {
					return 0;
				} else if (o2.getVelocityDiff() < o1.getVelocityDiff()) {
					return -1;
				} else {
					return 1;
				}
			}
		}).collect(Collectors.<AccelerationBounds>toList());
	}

}
