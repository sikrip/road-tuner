package sikrip.roaddyno.engine.dynosim;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;
import sikrip.roaddyno.engine.dynosim.RPMCaclulator;

/**
 * Tests for {@link RPMCaclulator}.
 */
public class RPMCalculatorTests {

	@Test
	public void verifyKPH_to_RPM() {
		double fgr = 4.3;
		double gr = 1.310;
		double tyreDiameter = 590;

		double rpm = Math.floor(RPMCaclulator.getRPM(147, fgr, gr, tyreDiameter));
		assertEquals(7451.0, rpm);
	}
}
