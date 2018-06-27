package sikrip.roaddyno.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LogEntry {

	private final Map<String, LogValue<Double>> values;
	private final String timeKey;
	private final String velocityKey;
	private final String tpsKey;

	public LogEntry(Map<String, LogValue<Double>> values, String timeKey, String velocityKey) {
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

	public LogEntry getCopy() {
		final Map<String, LogValue<Double>> valuesCopy = new HashMap<>();

		for (Map.Entry<String, LogValue<Double>> valueEntry : values.entrySet()) {
			LogValue<Double> value = valueEntry.getValue();
			valuesCopy.put(valueEntry.getKey(), new LogValue<>(value.getValue(), value.getUnit()));
		}

		return new LogEntry(valuesCopy, timeKey, velocityKey);
	}

	public Set<String> getDataKeys() {
		return values.keySet();
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder("LogEntry ");

		for (Map.Entry<String, LogValue<Double>> valueEntry : values.entrySet()) {
			stringBuilder.append(valueEntry.getKey()).append(": ").append(valueEntry.getValue()).append(" ");
		}

		return stringBuilder.toString();
	}
}
