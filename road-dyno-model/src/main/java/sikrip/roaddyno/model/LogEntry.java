package sikrip.roaddyno.model;

import java.util.HashMap;
import java.util.Map;

public class LogEntry {

	private final Map<String, LogValue<Double>> values;
	private final String timeKey;
	private final String rpmKey;
	private final String tpsKey;

	public LogEntry(Map<String, LogValue<Double>> values, String timeKey, String rpmKey, String tpsKey) {
		this.timeKey = timeKey;
		this.rpmKey = rpmKey;
		this.tpsKey = tpsKey;
		this.values = values;
	}

	public LogValue<Double> getTime() {
		return values.get(timeKey);
	}

	public LogValue<Double> getRpm() {
		return values.get(rpmKey);
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

		return new LogEntry(valuesCopy, timeKey, rpmKey, tpsKey);
	}

	@Override public String toString() {
		return String.format("LogEntry Time %s, RPM %s, TPS %s", getTime(), getRpm(), getTps());
	}
}
