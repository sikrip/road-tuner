package sikrip.roaddyno.engine;

import static junit.framework.TestCase.assertEquals;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.junit.Test;

public class TractiveEffortTests {

	@Test
	public void verifyPeakHPFunction() {

		final UnivariateFunction peakHpFunction = null;// find function for peak hp of tractive effort
		final double minSpeed = 10; // min speed with min gear
		final double maxSpeed = 200; // max speed with max gear

		final UnivariateIntegrator integrator = new SimpsonIntegrator();

		final double integrationOfPeakHP = 1000;
		assertEquals(integrationOfPeakHP, integrator.integrate(10, peakHpFunction, minSpeed, maxSpeed));
	}

	@Test
	public void verifySimpleIntegration() {
		UnivariateFunction uf = new PolynomialFunction(new double[] { 0, 1 });
		UnivariateIntegrator integrator = new SimpsonIntegrator();

		assertEquals(0.5, integrator.integrate(10, uf, 0, 1));
	}

}
