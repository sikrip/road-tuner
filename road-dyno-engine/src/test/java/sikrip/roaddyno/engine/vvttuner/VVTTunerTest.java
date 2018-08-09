package sikrip.roaddyno.engine.vvttuner;

import org.junit.Test;
import sikrip.roaddyno.logreader.DatalogitLogReader;
import sikrip.roaddyno.model.InvalidLogFileException;
import sikrip.roaddyno.model.RunData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static sikrip.roaddyno.engine.WotRunDetectorTest.getTestResourceUrl;
import static sikrip.roaddyno.engine.vvttuner.VVTTuner.tuneVVT;

public class VVTTunerTest {



    @Test
    public void verifyTuneVVT() throws IOException, InvalidLogFileException {

        final DatalogitLogReader logReader = new DatalogitLogReader();
        final List<RunData> runDataList = new ArrayList<>();

        runDataList.add(new RunData(
                true,
                "lc-10",
                logReader.readLog(getTestResourceUrl("/vvt-logs/lc-10.txt").getPath())
        ));

        runDataList.add(new RunData(
                true,
                "lc-20",
                logReader.readLog(getTestResourceUrl("/vvt-logs/lc-20.txt").getPath())
        ));

        runDataList.add(new RunData(
                true,
                "lc-30",
                logReader.readLog(getTestResourceUrl("/vvt-logs/lc-30.txt").getPath())
        ));

        runDataList.add(new RunData(
                true,
                "lc-40",
                logReader.readLog(getTestResourceUrl("/vvt-logs/lc-40.txt").getPath())
        ));

        runDataList.add(new RunData(
                true,
                "lc-50",
                logReader.readLog(getTestResourceUrl("/vvt-logs/lc-50.txt").getPath())
        ));

        runDataList.add(new RunData(
                true,
                "hc-10",
                logReader.readLog(getTestResourceUrl("/vvt-logs/hc-10.txt").getPath())
        ));

        runDataList.add(new RunData(
                true,
                "hc-20",
                logReader.readLog(getTestResourceUrl("/vvt-logs/hc-20.txt").getPath())
        ));

        runDataList.add(new RunData(
                true,
                "hc-30",
                logReader.readLog(getTestResourceUrl("/vvt-logs/hc-30.txt").getPath())
        ));

        runDataList.add(new RunData(
                true,
                "hc-40",
                logReader.readLog(getTestResourceUrl("/vvt-logs/hc-40.txt").getPath())
        ));

        final Map<Double, RunData> doubleRunDataMap = tuneVVT(runDataList);

        doubleRunDataMap.keySet().stream().sorted().forEachOrdered(rpm -> {
            System.out.println(String.format("%s\t%s", rpm.intValue(), doubleRunDataMap.get(rpm)));
        });
    }
}