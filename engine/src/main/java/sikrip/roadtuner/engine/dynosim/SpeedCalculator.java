package sikrip.roadtuner.engine.dynosim;

/**
 * Calculates the speed based on RPM, Gearing and Tyre.
 */
final class SpeedCalculator {

	final static double PI = 3.14;

	private SpeedCalculator() {
		// do not allow instantiation
	}

	/**
	 * Gets the speed in feet per second for the given rpm, gearing and tyre.
	 *
	 * @param rpm
	 * 		the rpm
	 * @param fgr
	 * 		the final gear ratio
	 * @param gr
	 * 		the gear ratio
	 * @param tyreDiameter
	 * 		the tyre diameter(in mm)
	 * @return the speed in feet per second
	 */
	static double getFeetPerSecond(double rpm, double fgr, double gr, double tyreDiameter) {

		double wheelRPM = rpm / (fgr * gr);
		double wheelRadiusInFeet = (tyreDiameter / 2) * 0.00328; //ft
		double radPerSec = wheelRPM * 2 * PI / 60;

		return radPerSec * wheelRadiusInFeet;
	}

	/**
	 * Gets the speed in meter per second for the given rpm, gearing and tyre.
	 *
	 * @param rpm
	 * 		the rpm
	 * @param fgr
	 * 		the final gear ratio
	 * @param gr
	 * 		the gear ratio
	 * @param tyreDiameter
	 * 		the tyre diameter(in mm)
	 * @return the speed in meter per second
	 */
	static double getMeterPerSecond(double rpm, double fgr, double gr, double tyreDiameter) {
		return getFeetPerSecond(rpm, fgr, gr, tyreDiameter) * 0.3048;
	}

	/**
	 * Converts speed from km/h to m/s.
	 *
	 * @param kph
	 * 		the km/h value
	 * @return the speed in m/s
	 */
	static double getMeterPerSecond(double kph) {
		return kph * (1000.0 / 3600.0);
	}
}
