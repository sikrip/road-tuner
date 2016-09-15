package sikrip.roaddyno.model;

import java.util.HashMap;
import java.util.Map;

public class LogEntry {

	private final Map<String, LogValue<Double>> values;
	private final String timeKey;
	private final String velocityKey;
	private final String tpsKey;

	public LogEntry(Map<String, LogValue<Double>> values, String timeKey, String velocityKey, String tpsKey) {
		this.values = new HashMap<>(values);
		this.timeKey = timeKey;
		this.velocityKey = velocityKey;
		this.tpsKey = tpsKey;
	}

	public LogEntry(Map<String, LogValue<Double>> values, String timeKey, String velocityKey) {
		this(values, timeKey, velocityKey, null);
	}

	public LogValue<Double> getTime() {
		return values.get(timeKey);
	}

	public LogValue<Double> getVelocity() {
		return values.get(velocityKey);
	}

	public LogValue<Double> getTps() {
		return values.get(tpsKey);
	}

	public LogValue<Double> get(String valueKey){
		return values.get(valueKey);
	}

	public LogEntry getCopy(){
		Map<String, LogValue<Double>> valuesCopy = new HashMap<>();

		for (String valueKey : this.values.keySet()) {
			LogValue<Double> value = this.values.get(valueKey);
			valuesCopy.put(valueKey, new LogValue<>(value.getValue(), value.getUnit()));
		}

		return new LogEntry(valuesCopy, timeKey, velocityKey, tpsKey);
	}

	@Override
	public String toString() {
		return String.format("LogEntry Time %s, Velocity %s, TPS %s", getTime(), getVelocity(), getTps());
	}
}
