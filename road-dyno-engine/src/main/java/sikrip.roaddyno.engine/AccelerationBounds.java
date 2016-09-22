package sikrip.roaddyno.engine;

import sikrip.roaddyno.model.LogEntry;

/**
 * Holds the start and end index of an acceleration run.
 */
public final class AccelerationBounds {

	private final int start;
	private final int end;

	private final LogEntry startEntry;
	private final LogEntry endEntry;

	public AccelerationBounds(int start, int end, LogEntry startEntry, LogEntry endEntry) {
		this.start = start;
		this.end = end;
		this.startEntry = startEntry;
		this.endEntry = endEntry;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public LogEntry getStartEntry() {
		return startEntry;
	}

	public LogEntry getEndEntry() {
		return endEntry;
	}

	double getVelocityDiff() {
		return endEntry.getVelocity().getValue() - startEntry.getVelocity().getValue();
	}

	@Override
	public String toString() {
		return "AccelerationBounds{" +
				"start=" + start +
				", end=" + end +
				'}';
	}
}
