package sikrip.roadtuner.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	/**
	 * A collection of the headers contained in {@link #logEntries}.
	 */
	private final Set<String> dataHeaders;

	/**
	 * True for rpm based runs, false for speed based runs.
	 */
	private final boolean rpmBased;

	public DynoSimulationResult(final boolean rpmBased, final List<LogEntry> logEntries, final List<DynoSimulationEntry> entries) {
		if (logEntries == null || logEntries.isEmpty()) {
			throw new IllegalArgumentException("Cannot create dyno simulation result, no log entries provided");
		}
		this.rpmBased = rpmBased;
		this.logEntries = logEntries;
		this.entries = entries;
		dataHeaders = new HashSet<>(logEntries.get(0).getDataKeys());
	}

	public boolean isRpmBased() {
		return rpmBased;
	}

	public Set<String> getDataHeaders() {
		return dataHeaders;
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

	public DynoSimulationEntry getResultAt(double rpm) {
		for (DynoSimulationEntry entry : entries) {
			if (entry.getRpm() == rpm) {
				return entry;
			}
		}
		return null;
	}

	public LogValue<?> getLogEntryAt(double rpm, String field) {
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
}
