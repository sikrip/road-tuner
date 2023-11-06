package sikrip.roadtuner.logreader;

import sikrip.roadtuner.model.LogEntry;
import sikrip.roadtuner.model.LogValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Reads log files produced by PowerTune.
 */
public class PowerTuneLogReader {


    private static final String SEPARATOR = ",";
    private static final String TIME_HEADER_KEY = "Time(S)";
    private static final String RPM_HEADER_KEY = "RPM";
    private static final String TPS_HEADER_KEY = "VTA V";

    public static List<LogEntry> readEcuLog(InputStream logFileStream) throws IOException {
        final List<LogEntry> logData = new ArrayList<>();
        final List<String> headers = new ArrayList<>();
        try (final BufferedReader logReader = new BufferedReader(new InputStreamReader(logFileStream))) {
            String line;

            while ((line = logReader.readLine()) != null) {
                final List<String> values = Arrays.stream(line.split(SEPARATOR))
                    .map(String::trim).collect(Collectors.toList());
                if (headers.isEmpty()) {
                    headers.addAll(Arrays.asList(line.split(SEPARATOR)));
                } else {
                    Map<String, LogValue<Double>> valuesMap = new HashMap<>();
                    for (int i = 0; i < headers.size(); i++) {
                        final String header = headers.get(i);
                        valuesMap.put(
                            header,
                            new LogValue<>(Double.parseDouble(values.get(i)), "")
                        );
                    }
                    logData.add(new LogEntry(valuesMap, TIME_HEADER_KEY, RPM_HEADER_KEY, TPS_HEADER_KEY));
                }
            }
        }
        return logData;
    }
}
