package sikrip.roadtuner.engine.vvttuner;

import sikrip.roadtuner.model.LogEntry;
import sikrip.roadtuner.model.RunData;
import sikrip.roadtuner.model.WotRunBounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sikrip.roadtuner.engine.LogValuesUtilities.smoothValues;
import static sikrip.roadtuner.engine.LogValuesUtilities.smoothVelocity;
import static sikrip.roadtuner.engine.WotRunDetector.getWotRunBounds;

/**
 * Responsible to find best VVT settings based on logs from Apexi PFC.
 */
public final class VVTTuner {

    private static final String AIRFLOW_FIELD_NAME = "AirFlow";
    private static final long MIN_SAMPLES_FOR_MEAN_AIRFLOW = 2;

    private VVTTuner() {
        // no instantiation
    }

    public static Map<Double, RunData> tuneVVT(List<RunData> runDataList, double startRPM, double endRPM, double rpmStep) {

        final List<RunData> finalRunDataList = smoothRunData(trimRunData(runDataList));

        final Map<Double, RunData> bestRunPerRpm = new HashMap<>();

        for (double rpm = startRPM; rpm <= endRPM; rpm += rpmStep) {
            Double maxAirflow = null;
            RunData maxAirflowRun = null;
            for (RunData runData : finalRunDataList) {
                final Double meanAirfow = meanAirfow(runData, rpm, rpm + rpmStep);
                if (meanAirfow != null) {
                    if (maxAirflow == null) {
                        maxAirflow = meanAirfow;
                        maxAirflowRun = runData;
                    } else if (maxAirflow < meanAirfow) {
                        maxAirflow = meanAirfow;
                        maxAirflowRun = runData;
                    }
                }
            }
            bestRunPerRpm.put(rpm, maxAirflowRun);
        }
        return bestRunPerRpm;
    }

    private static Double meanAirfow(RunData runData, double rpmFrom, double rpmTo) {
        final Double airflowSum = runData.getLogEntries().stream()
                .filter(r -> r.getVelocity().getValue() >= rpmFrom && r.getVelocity().getValue() <= rpmTo)
                .map(r -> r.get(AIRFLOW_FIELD_NAME).getValue())
                .reduce(0.0, (i, j) -> i + j);
        final long count = runData.getLogEntries().stream()
                .filter(r -> r.getVelocity().getValue() >= rpmFrom && r.getVelocity().getValue() <= rpmTo)
                .count();

        if (airflowSum == 0 || count < MIN_SAMPLES_FOR_MEAN_AIRFLOW){
            return null;
        }
        return airflowSum / count;
    }

    private static List<RunData> trimRunData(List<RunData> runDataList) {
        final List<RunData> trimmedRunDataList = new ArrayList<>();
        runDataList.forEach(r -> {
            final List<WotRunBounds> wotRunBounds = getWotRunBounds(r.isRpmBased(), r.getLogEntries());
            if (!wotRunBounds.isEmpty()) {
                // TODO select best wot run
                final WotRunBounds wotRun = wotRunBounds.get(0);
                RunData trimmedRun = new RunData(
                        r.isRpmBased(),
                        r.getName(),
                        r.getLogEntries().subList(wotRun.getStart(), wotRun.getEnd())
                );
                trimmedRunDataList.add(trimmedRun);
            }
        });
        return trimmedRunDataList;
    }

    private static List<RunData> smoothRunData(List<RunData> runDataList) {
        final List<RunData> smoothedRunDataList = new ArrayList<>();
        runDataList.forEach(r -> {
            final List<LogEntry> smoothedLogEntries = smoothValues(
                smoothVelocity(r.getLogEntries()),
                AIRFLOW_FIELD_NAME
            );
            final RunData smoothedRunData = new RunData(r.isRpmBased(), r.getName(), smoothedLogEntries);
            smoothedRunDataList.add(smoothedRunData);
        });
        return smoothedRunDataList;
    }
}
