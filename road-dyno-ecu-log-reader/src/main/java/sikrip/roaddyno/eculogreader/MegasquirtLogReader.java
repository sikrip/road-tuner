package sikrip.roaddyno.eculogreader;

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

import sikrip.roaddyno.model.InvalidLogFileException;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.LogValue;

/**
 * Read log of mls format (megasquirt).
 */
public final class MegasquirtLogReader implements EcuLogReader {

	/**
	 * RPM value that one log entry RPM cannot be lower than the previous.
	 */
	public static final int RPM_NOISE_THRESHOLD = 800;

	@Override
	public final List<LogEntry> readLog(String filePath, double tpsStartThreshold) throws IOException, InvalidLogFileException {
		File logFile = new File(filePath);
		try (InputStream fileStream = new FileInputStream(logFile);) {
			return readLog(fileStream, tpsStartThreshold);
		} catch (IOException e) {
			throw e;
		}
	}

	@Override
	public List<LogEntry> readLog(InputStream inputStream, double tpsStartThreshold) throws IOException, InvalidLogFileException {
		BufferedReader logReader = new BufferedReader(new InputStreamReader(inputStream));

		List<String> headers = new ArrayList<>();
		List<String> units = new ArrayList<>();

		String timeColumnKey = null;
		String rpmColumnKey = null;
		String tpsColumnKey = null;

		String logLine;
		List<LogEntry> logEntries = new ArrayList<>();
		while ((logLine = logReader.readLine()) != null) {

			List<String> rawValues = Arrays.asList((logLine.split("\t")));

			if (rawValues.size() > 1) {
				if (headers.size() == 0) {
					// headers line
					headers.addAll(rawValues);
					timeColumnKey = headers.indexOf("Time") != -1 ? "Time" : null;
					rpmColumnKey = headers.indexOf("RPM") != -1 ? "RPM" : null;
					tpsColumnKey = headers.indexOf("TPS") != -1 ? "TPS" : null;

					if (timeColumnKey == null || rpmColumnKey == null || tpsColumnKey == null) {
						throw new InvalidLogFileException("Invalid log file format. Cannot find Time, RPM or TPS data.");
					}

				} else if (units.size() == 0) {
					// units line
					units.addAll(rawValues);
				} else {
					// data line
					LogEntry entry = createLogEntry(headers, units, rawValues, timeColumnKey, rpmColumnKey, tpsColumnKey);
					if (entry.get(tpsColumnKey).getValue() > tpsStartThreshold) {
						logEntries.add(entry);
					}
				}
			}
		}

		List<LogEntry> trimmedLogValues = trimByRPM(logEntries);

		if (trimmedLogValues.size() == 0) {
			throw new InvalidLogFileException("Invalid log file format. No log entries found.");
		}
		return trimmedLogValues;
	}

	/**
	 * Removes any entries that RPM was abruptly dropped (usually near the end of the WOT run).
	 *
	 * @param logEntries
	 * 		the initial log entries
	 * @return the trimmed log entries
	 */
	private List<LogEntry> trimByRPM(List<LogEntry> logEntries) {

		int maxIdx = logEntries.size();

		for (int i = 1; i < logEntries.size(); i++) {
			if (logEntries.get(i).getVelocity().getValue() - logEntries.get(i - 1).getVelocity().getValue() < -RPM_NOISE_THRESHOLD) {
				maxIdx = i - 1;
				break;
			}
		}
		if (maxIdx == logEntries.size()) {
			return logEntries;
		}
		return logEntries.subList(0, maxIdx);
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
		return new LogEntry(valuesMap, timeColumnKey, rpmColumnKey);
	}

}
