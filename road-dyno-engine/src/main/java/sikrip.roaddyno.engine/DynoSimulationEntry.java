package sikrip.roaddyno.engine;

import java.text.NumberFormat;

/**
 * A single result of a dyno simulation.
 */
public class DynoSimulationEntry {

	/**
	 * The RPM of the result.
	 */
	private final double rpm;

	/**
	 * The power in HP of the result.
	 */
	private final double power;

	DynoSimulationEntry(double rpm, double power) {
		this.rpm = rpm;
		this.power = power;
	}

	public double getRpm() {
		return rpm;
	}

	public double getPower() {
		return power;
	}

	public double getTorque() {
		// http://www.epi-eng.com/piston_engine_technology/power_and_torque.htm
		//HP = Torque x RPM ÷ 5252
		return power * 5252 / rpm;
	}

	@Override
	public String toString() {
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMaximumFractionDigits(0);
		return numberFormat.format(rpm) + "\t" + numberFormat.format(power);
	}
}
