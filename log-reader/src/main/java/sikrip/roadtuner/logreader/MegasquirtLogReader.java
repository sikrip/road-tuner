package sikrip.roadtuner.logreader;

import sikrip.roadtuner.model.InvalidLogFileException;
import sikrip.roadtuner.model.LogEntry;
import sikrip.roadtuner.model.LogValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Read log of mls format (megasquirt).
 */
public final class MegasquirtLogReader {

	private final static String TIME_HEADER_KEY = "Time";
	private final static String RPM_HEADER_KEY = "RPM";
	private final static String TPS_HEADER_KEY = "TPS";

	public final List<LogEntry> readLog(String filePath) throws IOException, InvalidLogFileException {
		final File logFile = new File(filePath);
		try (InputStream fileStream = new FileInputStream(logFile);) {
			return readLog(fileStream);
		}
	}

	public List<LogEntry> readLog(InputStream inputStream) throws IOException, InvalidLogFileException {
		final BufferedReader logReader = new BufferedReader(new InputStreamReader(inputStream));

		final List<String> headers = new ArrayList<>();
		final List<String> units = new ArrayList<>();
		final List<LogEntry> logEntries = new ArrayList<>();

		String timeColumnKey = null;
		String rpmColumnKey = null;
		String tpsColumnKey = null;
		String logLine;
		while ((logLine = logReader.readLine()) != null) {

			final List<String> rawValues = Arrays.asList((logLine.split("\t")));
			if (rawValues.size() > 1) {
				if (headers.size() == 0) {
					// headers line
					headers.addAll(rawValues);
					timeColumnKey = headers.indexOf(TIME_HEADER_KEY) != -1 ? TIME_HEADER_KEY : null;
					rpmColumnKey = headers.indexOf(RPM_HEADER_KEY) != -1 ? RPM_HEADER_KEY : null;
					tpsColumnKey = headers.indexOf(TPS_HEADER_KEY) != -1 ? TPS_HEADER_KEY : null;

					if (timeColumnKey == null || rpmColumnKey == null || tpsColumnKey == null) {
						throw new InvalidLogFileException("Invalid log file format. Cannot find Time, RPM or TPS data.");
					}
				} else if (units.size() == 0) {
					// units line
					units.addAll(rawValues);
				} else {
					// data line
					logEntries.add(createLogEntry(headers, units, rawValues, timeColumnKey, rpmColumnKey, tpsColumnKey));
				}
			}
		}
		if (logEntries.isEmpty()) {
			throw new InvalidLogFileException("Invalid log file format. No log entries found.");
		}
		return logEntries;
	}

	private static LogEntry createLogEntry(List<String> headers, List<String> units, List<String> values,
			String timeColumnKey, String rpmColumnKey, String tpsColumnKey) {

		Map<String, LogValue<Double>> valuesMap = new HashMap<>();
		for (int i = 0; i < values.size(); i++) {
			Double value = null;
			try {
				value = Double.valueOf(values.get(i));
			} catch (NumberFormatException e) {
				/*no op*/
			}
			valuesMap.put(i < headers.size() ? headers.get(i) : "Unknown",
					new LogValue<>(value, i < units.size() ? units.get(i) : "Unknown unit"));
		}
		return new LogEntry(valuesMap, timeColumnKey, rpmColumnKey, tpsColumnKey);
	}

}
