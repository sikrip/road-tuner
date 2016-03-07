package sikrip.roaddyno.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;

import sikrip.roaddyno.model.DynoSimulationEntry;
import sikrip.roaddyno.model.DynoSimulationResult;
import sikrip.roaddyno.model.LogEntry;

/**
 * Simulates a dyno for a given run based on the log values, gearing weight and aerodynamic attributes of the car.
 */
public final class DynoSimulator {

	public static final int MIN_LOG_ENTRIES_COUNT = 10;

	private DynoSimulator() {
	}

	/**
	 * Simulates a dyno for a given run based on the provided log values, gearing weight and aerodynamic attributes of the car.
	 *
	 * @param logEntries
	 * 		the log entries from the run
	 * @param fgr
	 * 		the final gear ratio of the car
	 * @param gr
	 * 		the gear ratio used during the run
	 * @param tyreDiameter
	 * 		the diameter of the drive tyre(mm)
	 * @param carWeight
	 * 		the curb weight of the car(kg)
	 * @param occupantsWeight
	 * 		weight of the occupants during the run(kg)
	 * @param fa
	 * 		the frontal area of the car (m^2)
	 * @param cd
	 * 		the coefficient of drag of the car
	 * @return a dyno run that holds the result of the dyno simulation
	 */
	public static DynoSimulationResult run(List<LogEntry> logEntries, double fgr, double gr, double tyreDiameter,
			double carWeight, double occupantsWeight, double fa, double cd) throws SimulationException {

		validateParameters(logEntries, fgr, gr, tyreDiameter, carWeight, occupantsWeight, fa, cd);

		Iterator<LogEntry> logEntryIterator = null;
		try {
			logEntryIterator = smoothRPM(logEntries).iterator();
		} catch (Exception e) {
			throw new SimulationException(e.getMessage());
		}
		LogEntry from = null;
		LogEntry to = null;
		List<DynoSimulationEntry> dynoRunEntries = new ArrayList<>();

		double totalWeight = carWeight + occupantsWeight;

		while (logEntryIterator.hasNext()) {
			if (from == null) {
				from = logEntryIterator.next();
			} else {
				from = to;
			}
			if (logEntryIterator.hasNext()) {
				to = logEntryIterator.next();

				double refRPM = (from.getRpm().getValue() + to.getRpm().getValue()) / 2;

				double fromSpeed = SpeedCalculator.getMeterPerSecond(from.getRpm().getValue(), fgr, gr, tyreDiameter);
				double toSpeed = SpeedCalculator.getMeterPerSecond(to.getRpm().getValue(), fgr, gr, tyreDiameter);

				double fromTime = from.getTime().getValue();
				double toTime = to.getTime().getValue();

				double accelerationPower = PowerCalculator.calculateAccelerationPower(totalWeight, fromSpeed, toSpeed, fromTime, toTime);
				double airDragPower = PowerCalculator.calculateDragPower(toSpeed, fa, cd);
				double rollingDragPower = PowerCalculator.calculateRollingDragPower(totalWeight, toSpeed);

				dynoRunEntries.add(new DynoSimulationEntry(refRPM, accelerationPower + airDragPower + rollingDragPower));
			}
		}

		return new DynoSimulationResult(logEntries, dynoRunEntries);
	}

	private static void validateParameters(List<LogEntry> logEntries, double fgr, double gr, double tyreDiameter,
			double carWeight, double occupantsWeight, double fa, double cd) throws SimulationException {
		if (logEntries.size() < MIN_LOG_ENTRIES_COUNT) {
			throw new SimulationException("Not enough log entries provided. Try a longer WOT run.");
		}
		if (fgr <= 0) {
			throw new SimulationException("Final gear ratio must be a positive number.");
		}
		if (gr <= 0) {
			throw new SimulationException("Gear ratio must be a positive number.");
		}
		if (tyreDiameter <= 0) {
			throw new SimulationException("Tyre diameter must be a positive number.");
		}
		if (carWeight <= 0) {
			throw new SimulationException("Curb weight must be a positive number.");
		}
		if (occupantsWeight <= 0) {
			throw new SimulationException("Occupants weight must be a positive number.");
		}
		if (fa <= 0) {
			throw new SimulationException("Frontal area must be a positive number.");
		}
		if (cd <= 0) {
			throw new SimulationException("Coefficient of drag must be a positive number.");
		}
	}

	/**
	 * Smooths the RPM values of the given log entries using the Local Regression Algorithm (Loess, Lowess).
	 *
	 * @param rawEntries
	 * 		the raw log entries
	 * @return the log entries with the RPM values smoothed
	 */
	private static List<LogEntry> smoothRPM(List<LogEntry> rawEntries) {

		List<LogEntry> smoothedEntries = new ArrayList<>();

		double[] timeValues = new double[rawEntries.size()];
		double[] rawRPMValues = new double[rawEntries.size()];

		for (int i = 0; i < rawEntries.size(); i++) {
			LogEntry logEntry = rawEntries.get(i);
			timeValues[i] = logEntry.getTime().getValue();
			rawRPMValues[i] = logEntry.getRpm().getValue();
		}

		double[] smoothedRPMValues = new LoessInterpolator().smooth(timeValues, rawRPMValues);

		for (int i = 0; i < rawEntries.size(); i++) {
			LogEntry logEntryCopy = rawEntries.get(i).getCopy();
			logEntryCopy.getRpm().setValue(smoothedRPMValues[i]);
			smoothedEntries.add(logEntryCopy);
		}

		return smoothedEntries;
	}
}
