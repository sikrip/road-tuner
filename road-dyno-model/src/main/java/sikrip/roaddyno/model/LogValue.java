package sikrip.roaddyno.model;

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

}
