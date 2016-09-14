package sikrip.roaddyno.engine;


final class AccelerationBounds {

	private final int start;
	private final int end;

	public AccelerationBounds(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	@Override public String toString() {
		return "AccelerationBounds{" +
				"start=" + start +
				", end=" + end +
				'}';
	}
}
