package sikrip.roadtuner.engine.vvttuner

import sikrip.roadtuner.logreader.DatalogitLogReader
import sikrip.roadtuner.model.RunData
import spock.lang.Specification
import spock.lang.Unroll

import static sikrip.roadtuner.engine.vvttuner.VVTTuner.tuneVVT

class VVTTunerTest extends Specification {

    @Unroll
    def "Best VVT for #rpm should be #runName"(double rpm, String runName) {
        given: "all the runs evaluation"
        final DatalogitLogReader logReader = new DatalogitLogReader();
        final List<RunData> runDataList = new ArrayList<>();

        runDataList.add(new RunData(
                true,
                "lc-10",
                logReader.readLog(getClass().getResource("/vvt-logs/lc-10.txt").getPath())
        ));

        runDataList.add(new RunData(
                true,
                "lc-20",
                logReader.readLog(getClass().getResource("/vvt-logs/lc-20.txt").getPath())
        ));

        runDataList.add(new RunData(
                true,
                "lc-30",
                logReader.readLog(getClass().getResource("/vvt-logs/lc-30.txt").getPath())
        ));

        runDataList.add(new RunData(
                true,
                "lc-40",
                logReader.readLog(getClass().getResource("/vvt-logs/lc-40.txt").getPath())
        ));

        runDataList.add(new RunData(
                true,
                "lc-50",
                logReader.readLog(getClass().getResource("/vvt-logs/lc-50.txt").getPath())
        ));

        runDataList.add(new RunData(
                true,
                "hc-10",
                logReader.readLog(getClass().getResource("/vvt-logs/hc-10.txt").getPath())
        ));

        runDataList.add(new RunData(
                true,
                "hc-20",
                logReader.readLog(getClass().getResource("/vvt-logs/hc-20.txt").getPath())
        ));

        runDataList.add(new RunData(
                true,
                "hc-30",
                logReader.readLog(getClass().getResource("/vvt-logs/hc-30.txt").getPath())
        ));

        runDataList.add(new RunData(
                true,
                "hc-40",
                logReader.readLog(getClass().getResource("/vvt-logs/hc-40.txt").getPath())
        ));

        final Map<Double, RunData> doubleRunDataMap = tuneVVT(runDataList, 3000, 8000, 200);

        expect: "the best run for $rpm is $runName"
        doubleRunDataMap.get(rpm).getName() == runName

        where:
        rpm     |   runName
        3000	|	"lc-20"
        3200	|	"hc-20"
        3400	|	"hc-40"
        3600	|	"hc-20"
        3800	|	"lc-30"
        4000	|	"lc-20"
        4200	|	"lc-40"
        4400	|	"lc-40"
        4600	|	"lc-40"
        4800	|	"lc-40"
        5000	|	"lc-40"
        5200	|	"lc-40"
        5400	|	"lc-40"
        5600	|	"lc-40"
        5800	|	"hc-10"
        6000	|	"hc-10"
        6200	|	"hc-10"
        6400	|	"hc-10"
        6600	|	"hc-10"
        6800	|	"hc-10"
        7000	|	"hc-10"
        7200	|	"hc-20"
        7400	|	"hc-20"
        7600	|	"hc-30"
        7800	|	"hc-30"
        8000	|	"hc-30"
    }

}
