package sikrip.roaddyno.engine.vvttuner;

import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.RunData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static sikrip.roaddyno.engine.LogValuesUtilities.getByVelocity;
import static sikrip.roaddyno.engine.LogValuesUtilities.smoothValues;
import static sikrip.roaddyno.engine.LogValuesUtilities.smoothVelocity;
import static sikrip.roaddyno.engine.WotRunDetector.getWotRunBounds;

/**
 * Responsible to find best VVT settings based on logs from Apexi PFC.
 */
public final class VVTTuner {

    private static final String AIRFLOW_FIELD_NAME = "AirFlow";

    private VVTTuner() {
        // no instantiation
    }

    public static Map<Double, RunData> tuneVVT(List<RunData> runDataList) {
        final List<RunData> smoothedRunDataList = getSmoothedRunData(runDataList);

        final Double minRPM = smoothedRunDataList.stream().map(
            r -> r.getWotRunBounds().get(0).getStartEntry().getVelocity().getValue()
        ).min(Double::compare).get();

        final Double maxRPM = smoothedRunDataList.stream().map(
                r -> r.getWotRunBounds().get(0).getEndEntry().getVelocity().getValue()
        ).max(Double::compare).get();

        final List<Double> rpmValuesUnion = getRpmValuesUnion(smoothedRunDataList);
                //.stream().filter(rpm -> rpm >= minRPM && rpm <= maxRPM).collect(Collectors.toList());

        final Map<Double, RunData> bestRunPerRpm = new HashMap<>();
        rpmValuesUnion.forEach(rpm -> {
            Double maxAirflow = null;
            RunData maxAirflowRun = null;
            for (RunData runData : smoothedRunDataList) {
                final LogEntry logEntryForRPM = getByVelocity(runData.getLogEntries(), rpm);
                if (logEntryForRPM != null) {
                    final Double currentAirflow = logEntryForRPM.get(AIRFLOW_FIELD_NAME).getValue();
                    if (maxAirflow == null) {
                        maxAirflow = currentAirflow;
                        maxAirflowRun = runData;
                    } else {
                        if (maxAirflow < currentAirflow) {
                            maxAirflow = currentAirflow;
                            maxAirflowRun = runData;
                        }
                    }
                }
            }
            assert maxAirflow != null;
            bestRunPerRpm.put(rpm, maxAirflowRun);
        });
        return bestRunPerRpm;
    }

    private static List<Double> getRpmValuesUnion(List<RunData> runs) {
        return runs.stream()
                   .flatMap(r -> r.getLogEntries().stream().map(e -> e.getVelocity().getValue()))
                   .sorted()
                   .collect(Collectors.toList());
    }

    private static List<RunData> getSmoothedRunData(List<RunData> runDataList) {
        final List<RunData> smoothedRunDataList = new ArrayList<>();
        runDataList.forEach(r -> {
            final List<LogEntry> smoothedRPMEntries = smoothVelocity(r.getLogEntries());
            final List<LogEntry> smoothedLogEntries = smoothValues(
                smoothedRPMEntries,
                AIRFLOW_FIELD_NAME
            );
            final RunData smoothedRunData = new RunData(r.isRpmBased(), smoothedLogEntries);
            smoothedRunData.setWotRunBounds(getWotRunBounds(r.isRpmBased(), r.getLogEntries()));

            smoothedRunDataList.add(smoothedRunData);
        });
        return smoothedRunDataList;
    }
}
