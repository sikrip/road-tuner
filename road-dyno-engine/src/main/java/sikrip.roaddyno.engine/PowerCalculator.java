package sikrip.roaddyno.engine;

final class PowerCalculator {

	private final static double WATTS_PER_HP = 745.7;

	private PowerCalculator() {
	}

	/**
	 * Calculates the power in HP for the given acceleration and weight values.
	 *
	 * @param weight    the weight of the car (kg)
	 * @param fromSpeed the initial speed (m/sec)
	 * @param toSpeed   the final speed (m/sec)
	 * @param fromTime  the starting time (sec)
	 * @param toTime    the end time (sec)
	 * @return the power in HP for the given acceleration and weight values
	 */
	static double calculateAccelerationPower(double weight, double fromSpeed, double toSpeed, double fromTime, double toTime) {
		// power = energy / time
		double powerWatt =
				(calculateKineticEnergy(toSpeed, weight) - calculateKineticEnergy(fromSpeed, weight))
						/
						(toTime - fromTime);

		// convert watt to hp
		return powerWatt / WATTS_PER_HP;
	}

	/**
	 * Calculates the power in HP needed to overcome air drag.
	 *
	 * @param speed in m/sec
	 * @param fa    frontal area in m^2
	 * @param cd    coefficient of drag
	 * @return power needed to overcome air drag in HP
	 */
	static double calculateDragPower(double speed, double fa, double cd) {
		//P (power) = (1/2) rho A Cd v^3
		// rho = air density = 1.28 kg/m^3
		// A = frontal area m^2
		// Cd coefficient of drag
		// v = speed mps
		double powerAirDragWatt = 0.5 * 1.28 * fa * cd * Math.pow(speed, 3);

		return powerAirDragWatt / WATTS_PER_HP;
	}

	/**
	 * Calculates the energy in Joules of a car of the provided weight when traveling with the given speed.
	 *
	 * @param speed  the speed to the car
	 * @param weight the weight of the car
	 * @return the energy in Joules of a car of the provided weight when traveling with the given speed
	 */
	static double calculateKineticEnergy(double speed, double weight) {
		//energy = 0.5 * kg * mps^2
		return 0.5 * weight * speed * speed;
	}

	/**
	 * Calculates the power in HP needed to overcome the rolling resistance of the tyres.
	 *
	 * @param weight the weight of the car in kg
	 * @param speed  the speed of the car im m/sec
	 * @return the power in HP needed to overcome the rolling resistance of the tyres
	 */
	static double calculateRollingDragPower(double weight, double speed) {
		// rolling resistance = weight x 0.013 x mph / 375
		return weight * 0.013 * speed * 2.23 / 375;
	}
}
