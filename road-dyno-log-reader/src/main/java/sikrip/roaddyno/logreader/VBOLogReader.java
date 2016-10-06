package sikrip.roaddyno.logreader;

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

public class VBOLogReader {

	private static final String TIME_KEY = "time";
	private static final String VELOCITY_KEY = "velocity kmh";    // FIXME this may be different btw vbo files
	private static final String HEIGHT_KEY = "height";

	public List<LogEntry> readLog(String filePath) throws IOException, InvalidLogFileException {
		File logFile = new File(filePath);
		try (InputStream fileStream = new FileInputStream(logFile);) {
			return readLog(fileStream);
		}
	}

	public List<LogEntry> readLog(InputStream inputStream) throws IOException, InvalidLogFileException {
		BufferedReader logReader = new BufferedReader(new InputStreamReader(inputStream));
		List<String> headers = readHeaders(logReader);
		return readData(logReader, headers);
	}

	private List<String> readHeaders(BufferedReader logReader) throws IOException, InvalidLogFileException {
		String logLine;
		boolean headersSectionFound = false;
		while ((logLine = logReader.readLine()) != null) {
			if ("[header]".equals(logLine)) {
				headersSectionFound = true;
				break;
			}

		}
		List<String> headers = new ArrayList<>();
		if (headersSectionFound) {
			while ((logLine = logReader.readLine()) != null) {
				if (!logLine.startsWith("[")) {
					if (!"".equals(logLine)) {
						headers.add(logLine);
					}
				} else {
					// End of headers
					break;
				}
			}
		}
		if (!headers.contains(VELOCITY_KEY)) {
			throw new InvalidLogFileException("Invalid log file: speed header not found.");
		} else if (!headers.contains(TIME_KEY)) {
			throw new InvalidLogFileException("Invalid log file: time header not found.");
		}
		return headers;
	}

	private List<LogEntry> readData(BufferedReader logReader, List<String> headers) throws IOException, InvalidLogFileException {
		String logLine;
		boolean dataSectionFound = false;
		while ((logLine = logReader.readLine()) != null) {
			if ("[data]".equals(logLine)) {
				dataSectionFound = true;
				break;
			}

		}
		List<LogEntry> data = new ArrayList<>();
		if (dataSectionFound) {
			while ((logLine = logReader.readLine()) != null) {
				List<String> rawValues = getRawValues(logLine, headers.size());
				Map<String, LogValue<Double>> logEntryValues = new HashMap<>();
				for (int i = 0; i < rawValues.size(); i++) {
					final String header = headers.get(i);
					switch (header) {
					case TIME_KEY:
						logEntryValues.put(TIME_KEY, new LogValue<>(Double.valueOf(rawValues.get(i)), "sec"));
						break;
					case VELOCITY_KEY:
						logEntryValues.put(VELOCITY_KEY, new LogValue<>(Double.valueOf(rawValues.get(i)), "km/h"));
						break;
					case HEIGHT_KEY:
						// TODO find the units of this (not sure it is meters)
						logEntryValues.put(HEIGHT_KEY, new LogValue<>(Double.valueOf(rawValues.get(i)), "meters"));
						break;
					}
				}
				LogEntry logEntry = new LogEntry(logEntryValues, TIME_KEY, VELOCITY_KEY);
				if (!data.isEmpty()) {
					final double currentTime = logEntry.getTime().getValue();
					final double prevTime = data.get(data.size() - 1).getTime().getValue();
					if (currentTime < prevTime) {
						throw new InvalidLogFileException(
								String.format("Invalid log file: found non-increasing time values (%s => %s)", prevTime, currentTime));
					}
				}
				data.add(logEntry);
			}
		}
		return data;
	}

	private List<String> getRawValues(String logLine, int expectedNumberOfValues) throws InvalidLogFileException {
		List<String> rawValues = Arrays.asList((logLine.split(" ")));
		if (rawValues.size() == expectedNumberOfValues) {
			return rawValues;
		}
		rawValues = Arrays.asList((logLine.split(",")));
		if (rawValues.size() == expectedNumberOfValues) {
			return rawValues;
		}
		rawValues = Arrays.asList((logLine.split("\t")));
		if (rawValues.size() == expectedNumberOfValues) {
			return rawValues;
		}
		throw new InvalidLogFileException("Invalid log file. Expecting " + expectedNumberOfValues + " values, and found " + rawValues.size());
	}

}
