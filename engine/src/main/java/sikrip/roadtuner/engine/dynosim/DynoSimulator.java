package sikrip.roadtuner.engine.dynosim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sikrip.roadtuner.engine.LogValuesUtilities;
import sikrip.roadtuner.model.DynoSimulationEntry;
import sikrip.roadtuner.model.DynoSimulationResult;
import sikrip.roadtuner.model.LogEntry;
import sikrip.roadtuner.model.SimulationException;

/**
 * Simulates a dyno for a given run based on the log values, gearing weight and aerodynamic attributes of the car.
 */
public final class DynoSimulator {

	private static final int MIN_LOG_ENTRIES_COUNT = 10;

	private DynoSimulator() {
	}

	/**
	 * Simulates a dyno for a given run based on the provided log values, gearing weight and aerodynamic attributes of the car.
	 * The log entries must have RPM as velocity indicator.
	 *
	 * @param rpmLogEntries
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
	public static DynoSimulationResult runByRPM(List<LogEntry> rpmLogEntries, double fgr, double gr, double tyreDiameter,
												double carWeight, double occupantsWeight, double fa, double cd) throws SimulationException {

		validateParameters(rpmLogEntries, fgr, gr, tyreDiameter, carWeight, occupantsWeight, fa, cd);

		Iterator<LogEntry> logEntryIterator;
		try {
			logEntryIterator = LogValuesUtilities.smoothVelocity(rpmLogEntries).iterator();
		} catch (Exception e) {
			throw new SimulationException("Could not smooth rpm values.", e);
		}
		LogEntry from = null;
		LogEntry to = null;
		List<DynoSimulationEntry> dynoRunEntries = new ArrayList<>();

		final double totalWeight = carWeight + occupantsWeight;

		while (logEntryIterator.hasNext()) {
			if (from == null) {
				from = logEntryIterator.next();
			} else {
				from = to;
			}
			if (logEntryIterator.hasNext()) {
				to = logEntryIterator.next();

				double refRPM = (from.getVelocity().getValue() + to.getVelocity().getValue()) / 2;

				double fromSpeed = SpeedCalculator.getMeterPerSecond(from.getVelocity().getValue(), fgr, gr, tyreDiameter);
				double toSpeed = SpeedCalculator.getMeterPerSecond(to.getVelocity().getValue(), fgr, gr, tyreDiameter);

				double fromTime = from.getTime().getValue();
				double toTime = to.getTime().getValue();

				double accelerationPower = PowerCalculator.calculateAccelerationPower(totalWeight, fromSpeed, toSpeed, fromTime, toTime);
				double airDragPower = PowerCalculator.calculateDragPower(toSpeed, fa, cd);
				double rollingDragPower = PowerCalculator.calculateRollingDragPower(totalWeight, toSpeed);

				dynoRunEntries.add(new DynoSimulationEntry(refRPM, accelerationPower + airDragPower + rollingDragPower));
			}
		}

		return new DynoSimulationResult(true, rpmLogEntries, dynoRunEntries);
	}

	/**
	 * Simulates a dyno for a given run based on the provided log values, gearing weight and aerodynamic attributes of the car.
	 * The log entries must have speed in km/h as velocity indicator.
	 *
	 * @param speedLogEntries
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
	public static DynoSimulationResult runBySpeed(List<LogEntry> speedLogEntries, double fgr, double gr, double tyreDiameter,
			double carWeight, double occupantsWeight, double fa, double cd) throws SimulationException {

		validateParameters(speedLogEntries, fgr, gr, tyreDiameter, carWeight, occupantsWeight, fa, cd);

		LogEntry from = null;
		LogEntry to = null;
		List<DynoSimulationEntry> dynoRunEntries = new ArrayList<>();


		Iterator<LogEntry> logEntryIterator = null;
		try {
			logEntryIterator = LogValuesUtilities.smoothVelocity(speedLogEntries).iterator();
		} catch (Exception e) {
			throw new SimulationException("Could not smooth speed values.", e);
		}

		final double totalWeight = carWeight + occupantsWeight;

		while (logEntryIterator.hasNext()) {
			if (from == null) {
				from = logEntryIterator.next();
			} else {
				from = to;
			}
			if (logEntryIterator.hasNext()) {
				to = logEntryIterator.next();

				final double refSpeed = (from.getVelocity().getValue() + to.getVelocity().getValue()) / 2;

				final double refRPM = RPMCaclulator.getRPM(refSpeed, fgr, gr, tyreDiameter);

				final double fromSpeed = SpeedCalculator.getMeterPerSecond(from.getVelocity().getValue());
				final double toSpeed = SpeedCalculator.getMeterPerSecond(to.getVelocity().getValue());

				final double fromTime = from.getTime().getValue();
				final double toTime = to.getTime().getValue();

				final double accelerationPower = PowerCalculator.calculateAccelerationPower(totalWeight, fromSpeed, toSpeed, fromTime, toTime);
				final double airDragPower = PowerCalculator.calculateDragPower(toSpeed, fa, cd);
				final double rollingDragPower = PowerCalculator.calculateRollingDragPower(totalWeight, toSpeed);

				dynoRunEntries.add(new DynoSimulationEntry(refRPM, accelerationPower + airDragPower + rollingDragPower));
			}
		}
		// TODO raw log entries are speed based, should be replaced with rpm?
		return new DynoSimulationResult(false, speedLogEntries, dynoRunEntries);
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
}
