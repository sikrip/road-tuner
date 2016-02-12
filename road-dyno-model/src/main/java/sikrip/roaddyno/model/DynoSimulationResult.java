package sikrip.roaddyno.model;

import java.util.List;

/**
 * A dyno simulation result.
 */
public class DynoSimulationResult implements DynoRunInfo {

	/**
	 * The raw log entries that produced this result.
	 */
	private final List<LogEntry> logEntries;

	/**
	 * The result entries of the simulation.
	 */
	private final List<DynoSimulationEntry> entries;

	/**
	 * A name for the run.
	 */
	private final String name;

	/**
	 * The final gear ratio.
	 */
	private final double fgr;

	/**
	 * The ration of the gear used in the run.
	 */
	private final double gr;

	/**
	 * The diameter of the driving tyres in mm.
	 */
	private final double tyreDiameter;

	/**
	 * The total weight of the vehicle in kg.
	 */
	private final double carWeight;

	/**
	 * The total weight of the occupants in kg.
	 */
	private final double occupantsWeight;

	/**
	 * The frontal area of the car in m^2.
	 */
	private final double fa;

	/**
	 * The coefficient of drag of the car.
	 */
	private final double cd;

	public DynoSimulationResult(List<LogEntry> logEntries, List<DynoSimulationEntry> entries, String name, double fgr, double gr,
			double tyreDiameter, double carWeight, double occupantsWeight, double fa, double cd) {
		this.logEntries = logEntries;
		this.entries = entries;
		this.name = name;
		this.fgr = fgr;
		this.gr = gr;
		this.tyreDiameter = tyreDiameter;
		this.carWeight = carWeight;
		this.occupantsWeight = occupantsWeight;
		this.fa = fa;
		this.cd = cd;
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

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getFinalGearRatio() {
		return fgr;
	}

	@Override
	public double getGearRatio() {
		return gr;
	}

	@Override
	public double getTyreDiameter() {
		return tyreDiameter;
	}

	@Override
	public double getCarWeight() {
		return carWeight;
	}

	@Override
	public double getOccupantsWeight() {
		return occupantsWeight;
	}

	@Override
	public double getFrontalArea() {
		return fa;
	}

	@Override
	public double getCoefficientOfDrag() {
		return cd;
	}

	@Override
	public String toString() {
		return name;
	}
}
