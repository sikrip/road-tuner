package sikrip.roaddyno.engine;

import org.junit.Test;
import sikrip.roaddyno.logreader.DatalogitLogReader;
import sikrip.roaddyno.model.InvalidLogFileException;
import sikrip.roaddyno.model.LogEntry;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static sikrip.roaddyno.engine.LogValuesUtilities.smoothVelocity;
import static sikrip.roaddyno.engine.WotRunDetectorTest.getTestResourceUrl;

public class LogValuesUtilitiesTest {

    @Test
    public void verifySmoothVelocity() throws IOException, InvalidLogFileException {
        final DatalogitLogReader logReader = new DatalogitLogReader();

        final List<LogEntry> logEntries = smoothVelocity(logReader.readLog(getTestResourceUrl("/vvt-logs/lc-20.txt").getPath()));

        assertTrue("", logEntries.stream().mapToDouble(e -> e.getVelocity().getValue()).max().getAsDouble() > 6000);
    }
}