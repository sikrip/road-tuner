package sikrip.roaddyno.engine;

/**
 * Holds the start and end index of an acceleration run.
 */
public final class AccelerationBounds {

	private final int start;
	private final int end;


	AccelerationBounds(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	@Override
	public String toString() {
		return "AccelerationBounds{" +
				"start=" + start +
				", end=" + end +
				'}';
	}
}
