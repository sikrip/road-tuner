package sikrip.roaddyno.engine.vvttuner;

import org.junit.Test;
import sikrip.roaddyno.logreader.DatalogitLogReader;
import sikrip.roaddyno.model.InvalidLogFileException;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.RunData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static sikrip.roaddyno.engine.WotRunDetectorTest.getTestResourceUrl;
import static sikrip.roaddyno.engine.vvttuner.VVTTuner.tuneVVT;

public class VVTTunerTest {



    @Test
    public void verifyTuneVVT() throws IOException, InvalidLogFileException {

        final DatalogitLogReader logReader = new DatalogitLogReader();
        final List<RunData> runDataList = new ArrayList<>();

        runDataList.add(new RunData(
            true,
            logReader.readLog(getTestResourceUrl("/vvt-logs/lc-10.txt").getPath())
        ));

        /*runDataList.add(new RunData(
                true,
                logReader.readLog(getTestResourceUrl("/vvt-logs/lc-20.txt").getPath())
        ));*/

        final Map<Double, RunData> doubleRunDataMap = tuneVVT(runDataList);

        System.out.println(doubleRunDataMap);
    }
}