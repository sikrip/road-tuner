package sikrip.roadtuner.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a single log data line.
 */
public class LogEntry {

	private final Map<String, LogValue<Double>> values;
	private final String timeKey;
	private final String velocityKey;
	private final String tpsKey;

	public LogEntry(Map<String, LogValue<Double>> values, String timeKey, String velocityKey) {
		assert timeKey != null;
		assert velocityKey != null;
		this.values = new HashMap<>(values);
		this.timeKey = timeKey;
		this.velocityKey = velocityKey;
		this.tpsKey = null;
	}

	public LogEntry(Map<String, LogValue<Double>> values, String timeKey, String velocityKey, String tpsKey) {
		this.values = new HashMap<>(values);
		this.timeKey = timeKey;
		this.velocityKey = velocityKey;
		this.tpsKey = tpsKey;
	}

	public LogValue<Double> getTime() {
		return values.get(timeKey);
	}

	public LogValue<Double> getVelocity() {
		return values.get(velocityKey);
	}

	public LogValue<Double> getTps() {
		return tpsKey !=null ? values.get(tpsKey) : null;
	}

	public LogValue<Double> get(String valueKey) {
		return values.get(valueKey);
	}

	public void set(String valueKey, double value, String unit) {
		values.put(valueKey, new LogValue<>(value, unit));
	}

	public LogEntry getCopy() {
		final Map<String, LogValue<Double>> valuesCopy = new HashMap<>();

		for (Map.Entry<String, LogValue<Double>> valueEntry : values.entrySet()) {
			LogValue<Double> value = valueEntry.getValue();
			valuesCopy.put(valueEntry.getKey(), new LogValue<>(value.getValue(), value.getUnit()));
		}

		return new LogEntry(valuesCopy, timeKey, velocityKey);
	}

	public String getTimeKey() {
		return timeKey;
	}

	public Set<String> getDataKeys() {
		return values.keySet();
	}

	public String getVelocityKey() {
		return velocityKey;
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder("LogEntry ");

		for (Map.Entry<String, LogValue<Double>> valueEntry : values.entrySet()) {
			stringBuilder.append(valueEntry.getKey()).append(": ").append(valueEntry.getValue()).append(" ");
		}

		return stringBuilder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof LogEntry)) {
			return false;
		}
		LogEntry logEntry = (LogEntry) o;
		return Objects.equals(getTime(), logEntry.getTime());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTime());
	}
}
