package sikrip.roaddyno.engine;

import static junit.framework.TestCase.assertEquals;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.junit.Before;
import org.junit.Test;

public class PowerCalculatorTest {

    private DecimalFormat df;

    @Before
    public void init() {
        df = new DecimalFormat("#.0");
        df.setRoundingMode(RoundingMode.DOWN);
    }

    @Test
    public void verifyAccelerationPower(){
        // http://www.dummies.com/how-to/content/how-to-calculate-power-based-on-force-and-speed.html
        assertEquals("78.0", df.format(PowerCalculator.calculateAccelerationPower(1100, 0, 23, 0, 5)));
    }

    @Test
    public void verifyAirDragPower() {
        // https://www.physicsforums.com/threads/drag-equation-derivation.305119/
        assertEquals("14.7", df.format(PowerCalculator.calculateDragPower(27.8, 2.5, 0.32)));
    }
}
