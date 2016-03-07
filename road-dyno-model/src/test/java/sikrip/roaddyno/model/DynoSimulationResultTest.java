package sikrip.roaddyno.model;



import static junit.framework.TestCase.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Tests for {@link DynoSimulationResult}.
 */
public class DynoSimulationResultTest {

	@Test
	public void validateMaxPower() {
		List<DynoSimulationEntry> results = new ArrayList<>();
		for (double i = 1; i < 10; i++) {
			double power = 10.0 * i;
			if(i==9.0){
				power-=15;
			}
			results.add(new DynoSimulationEntry(2000.0 + i * 700, power));
		}
		DynoSimulationResult result = new DynoSimulationResult(null, results);
		assertEquals(80.0, result.maxPower().getPower());
		assertEquals(7600.0, result.maxPower().getRpm());
	}

	@Test
	public void validateMaxTorgue() {
		List<DynoSimulationEntry> results = new ArrayList<>();
		for (double i = 1; i < 10; i++) {
			double power = 10.0 * i;
			if(i==9.0){
				power-=5;
			}
			results.add(new DynoSimulationEntry(2000.0 + i * 700, power));
		}
		DynoSimulationResult result = new DynoSimulationResult(null, results);
		assertEquals(80.0 * 5252 / 7600, result.maxTorque().getTorque());
	}
}
