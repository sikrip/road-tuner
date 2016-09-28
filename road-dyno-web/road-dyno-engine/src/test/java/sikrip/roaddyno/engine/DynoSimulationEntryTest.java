package sikrip.roaddyno.engine;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for {@link DynoSimulationEntry}.
 */
public class DynoSimulationEntryTest {

	@Test
	public void torgueAndPowerShouldBeEqualAt5252RPM(){
		DynoSimulationEntry dynoSimulationEntry = new DynoSimulationEntry(5252, 150);
		assertEquals(dynoSimulationEntry.getPower(), dynoSimulationEntry.getTorque(), 0.0);
	}
}
