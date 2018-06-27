package sikrip.roaddyno.logreader;

import sikrip.roaddyno.model.InvalidLogFileException;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.LogValue;

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
 * Reader for log files produced by datalogit/pfc.
 */
public class DatalogitLogReader {

	private static final String SEPARATOR = "\t";
	private static final String TIME_HEADER_KEY = "Time(S)";
	private static final String RPM_HEADER_KEY = "RPM";
	private static final String TPS_HEADER_KEY = "VTA V";

	public final List<LogEntry> readLog(String filePath) throws IOException, InvalidLogFileException {
		final File logFile = new File(filePath);
		try (InputStream fileStream = new FileInputStream(logFile);) {
			return readLog(fileStream);
		}
	}

	public List<LogEntry> readLog(InputStream inputStream) throws IOException, InvalidLogFileException {
		final BufferedReader logReader = new BufferedReader(new InputStreamReader(inputStream));

		final List<String> headers = readHeaders(logReader);

		final List<LogEntry> logEntries = readDataLines(logReader, headers);

		if (logEntries.isEmpty()) {
			throw new InvalidLogFileException("Invalid log file format. No log entries found.");
		}
		return logEntries;
	}

	private List<String> readHeaders(BufferedReader logReader) throws InvalidLogFileException {
		try {
			// Skip version line
			logReader.readLine();
			// Next line contains the headers
			return Arrays.asList((logReader.readLine().split(SEPARATOR)));
		} catch (Exception e) {
			throw new InvalidLogFileException("Invalid log file. Cannot read headers.");
		}
	}

	private List<LogEntry> readDataLines(BufferedReader logReader, List<String> headers) throws IOException, InvalidLogFileException {
		final List<LogEntry> logEntries = new ArrayList<>();
		final String timeColumnKey = headers.indexOf(TIME_HEADER_KEY) != -1 ? TIME_HEADER_KEY : null;
		final String rpmColumnKey = headers.indexOf(RPM_HEADER_KEY) != -1 ? RPM_HEADER_KEY : null;
		final String tpsColumnKey = headers.indexOf(TPS_HEADER_KEY) != -1 ? TPS_HEADER_KEY : null;
		if (timeColumnKey == null || rpmColumnKey == null) {
			throw new InvalidLogFileException("Invalid log file format. Cannot find Time, RPM or TPS data.");
		}
		String logLine;
		while ((logLine = logReader.readLine()) != null) {
			final List<String> lineValues = Arrays.asList((logLine.split(SEPARATOR)));
			if (!lineValues.isEmpty()) {
				logEntries.add(createLogEntry(headers, lineValues, timeColumnKey, rpmColumnKey, tpsColumnKey));
			}
		}
		return logEntries;
	}

	private static LogEntry createLogEntry(List<String> headers,
										   List<String> values,
										   String timeColumnKey,
										   String rpmColumnKey,
										   String tpsColumnKey) {
		final Map<String, LogValue<Double>> valuesMap = new HashMap<>();
		for (int i = 0; i < values.size(); i++) {
			Double value = null;
			try {
				value = Double.valueOf(values.get(i));
			} catch (NumberFormatException e) {
				/*no op*/
			}
			valuesMap.put(
				i < headers.size() ? headers.get(i) : "Unknown", new LogValue<>(value, "Unknown unit")
			);
		}
		return new LogEntry(valuesMap, timeColumnKey, rpmColumnKey, tpsColumnKey);
	}
}
