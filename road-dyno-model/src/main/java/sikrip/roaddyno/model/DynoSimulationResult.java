package sikrip.roaddyno.model;

import java.util.List;

/**
 * A dyno simulation result.
 */
public class DynoSimulationResult {

	/**
	 * The raw log entries that produced this result.
	 */
	private final List<LogEntry> logEntries;

	/**
	 * The result entries of the simulation.
	 */
	private final List<DynoSimulationEntry> entries;

	public DynoSimulationResult(List<LogEntry> logEntries, List<DynoSimulationEntry> entries) {
		this.logEntries = logEntries;
		this.entries = entries;
	}

	public DynoSimulationEntry minPower() {
		DynoSimulationEntry minPower = null;
		for (DynoSimulationEntry entry : entries) {
			if (minPower == null) {
				minPower = entry;
			} else if (entry.getPower() < minPower.getPower()) {
				minPower = entry;
			}
		}
		return minPower;
	}

	public DynoSimulationEntry maxPower() {
		DynoSimulationEntry maxPower = null;
		for (DynoSimulationEntry entry : entries) {
			if (maxPower == null) {
				maxPower = entry;
			} else if (entry.getPower() > maxPower.getPower()) {
				maxPower = entry;
			}
		}
		return maxPower;
	}

	public DynoSimulationEntry minTorque() {
		DynoSimulationEntry minTorque = null;
		for (DynoSimulationEntry entry : entries) {
			if (minTorque == null) {
				minTorque = entry;
			} else if (entry.getTorque() < minTorque.getTorque()) {
				minTorque = entry;
			}
		}
		return minTorque;
	}

	public DynoSimulationEntry maxTorque() {
		DynoSimulationEntry maxTorque = null;
		for (DynoSimulationEntry entry : entries) {
			if (maxTorque == null) {
				maxTorque = entry;
			} else if (entry.getTorque() > maxTorque.getTorque()) {
				maxTorque = entry;
			}
		}
		return maxTorque;
	}

	public DynoSimulationEntry getAt(double rpm) {
		for (DynoSimulationEntry entry : entries) {
			if (entry.getRpm() == rpm) {
				return entry;
			}
		}
		return null;
	}

	public LogValue<?> getAt(double rpm, String field) {
		for (int i = 0; i < entries.size(); i++) {
			DynoSimulationEntry entry = entries.get(i);
			if (entry.getRpm() == rpm) {
				return logEntries.get(i).get(field);
			}
		}
		return null;
	}

	public double[][] powerDataset() {
		double[][] dataset = new double[2][entries.size()];
		for (int i = 0; i < entries.size(); i++) {
			dataset[0][i] = entries.get(i).getRpm();
			dataset[1][i] = entries.get(i).getPower();
		}
		return dataset;
	}

	public double[][] torqueDataset() {
		double[][] dataset = new double[2][entries.size()];
		for (int i = 0; i < entries.size(); i++) {
			dataset[0][i] = entries.get(i).getRpm();
			dataset[1][i] = entries.get(i).getTorque();
		}
		return dataset;
	}

	public double getSmoothedRpmAt(int idx) {
		return entries.get(idx).getRpm();
	}

	public int getEntriesSize() {
		return entries.size();
	}

	public List<LogEntry> getLogEntries() {
		return logEntries;
	}

}
