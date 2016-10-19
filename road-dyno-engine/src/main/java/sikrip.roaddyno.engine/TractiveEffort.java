package sikrip.roaddyno.engine;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

public final class TractiveEffort {

	public double calculatePeakPowerIntegration(DynoSimulationResult dynoResult, double fgr, double tyreDiameter, double[] gearRatios) {
		final double[] peakTorqueSpeed = new double[gearRatios.length]; // x
		final double[] peakTorqueValue = new double[gearRatios.length];  // y

		final DynoSimulationEntry maxPower = dynoResult.maxPower();

		for (int i = 0; i < gearRatios.length; i++) {
			double gearRatio = gearRatios[i];
			peakTorqueValue[i] = maxPower.getTorque() * fgr * gearRatio;
			peakTorqueSpeed[i] = SpeedCalculator.getKPH(maxPower.getRpm(), fgr, tyreDiameter, gearRatio);
		}
		final UnivariateInterpolator interpolator = new SplineInterpolator();
		final UnivariateFunction peakTorqueFunction = interpolator.interpolate(peakTorqueSpeed, peakTorqueValue);

		final UnivariateIntegrator integrator = new SimpsonIntegrator();

		final double minSpeed = SpeedCalculator.getKPH(maxPower.getRpm(), fgr, tyreDiameter, gearRatios[0]);
		final double maxSpeed = SpeedCalculator.getKPH(dynoResult.getMaxRPM(), fgr, tyreDiameter, gearRatios[gearRatios.length-1]);

		return integrator.integrate(10, peakTorqueFunction, minSpeed, maxSpeed);
	}
}
