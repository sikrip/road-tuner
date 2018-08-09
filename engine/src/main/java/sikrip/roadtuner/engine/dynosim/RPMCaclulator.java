package sikrip.roaddyno.engine.dynosim;

/**
 * Converts speed to RPM.
 */
final class RPMCaclulator {

	private RPMCaclulator() {
		// do not allow instantiation
	}

	/**
	 * Gets the RPM for the given speed , gearing and tyre diameter.
	 *
	 * @param speedKPH
	 * 		speed, in km/h
	 * @param fgr
	 * 		final gear ratio
	 * @param gr
	 * 		gear ratio
	 * @param tyreDiameter
	 * 		tyre diameter in mm
	 * @return the RPM for the given speed , gearing and tyre diameter
	 */
	static double getRPM(double speedKPH, double fgr, double gr, double tyreDiameter) {
		// TODO replace the magic number with actual division
		double speedFeetPerSecond = 0.911344415 * speedKPH;
		return (speedFeetPerSecond * fgr * gr * 60) / (SpeedCalculator.PI * tyreDiameter * 0.00328);
	}
}
