package sikrip.roaddyno.engine;

import org.junit.Test;
import sikrip.roaddyno.logreader.DatalogitLogReader;
import sikrip.roaddyno.model.InvalidLogFileException;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.WotRunBounds;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static sikrip.roaddyno.engine.LogValuesUtilities.smoothVelocity;
import static sikrip.roaddyno.engine.WotRunDetector.getWotRunBounds;
import static sikrip.roaddyno.engine.WotRunDetectorTest.getTestResourceUrl;

public class LogValuesUtilitiesTest {

    @Test
    public void verifySmoothVelocity() throws IOException, InvalidLogFileException {

        final DatalogitLogReader logReader = new DatalogitLogReader();
        final List<LogEntry> rawEntries = logReader.readLog(getTestResourceUrl("/vvt-logs/lc-20.txt").getPath());
        final List<WotRunBounds> wotRunBounds = getWotRunBounds(true, rawEntries);
        final List<LogEntry> wotRawEntries = rawEntries.subList(wotRunBounds.get(0).getStart(), wotRunBounds.get(0).getEnd());
        final List<LogEntry> wotSmoothedEntries = smoothVelocity(wotRawEntries);

        for (int i=0; i< wotRawEntries.size(); i++) {
            assertTrue(
                "Smoothed value is within 100 rpm compared to raw value",
                Math.abs(wotSmoothedEntries.get(i).getVelocity().getValue() - wotRawEntries.get(i).getVelocity().getValue()) < 100
            );
        }
    }
}