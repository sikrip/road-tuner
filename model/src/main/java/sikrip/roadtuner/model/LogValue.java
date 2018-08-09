package sikrip.roadtuner.model;

import java.util.Objects;

/**
 * A single log value along with it's unit.
 *
 * @param <V> the value data type
 */
public class LogValue<V> {

	private V value;
	private final String unit;

	public LogValue(V value, String unit) {
		this.value = value;
		this.unit = unit;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	@Override
	public String toString() {
		return value + "(" + unit + ')';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof LogValue)) {
			return false;
		}
		LogValue<?> logValue = (LogValue<?>) o;
		return Objects.equals(value, logValue.value) &&
				Objects.equals(unit, logValue.unit);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, unit);
	}
}
