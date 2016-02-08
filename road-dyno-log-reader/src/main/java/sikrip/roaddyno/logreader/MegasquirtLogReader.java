package sikrip.roaddyno.logreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.LogValue;

/**
 * Read log of mls format (megasquirt).
 */
public final class MegasquirtLogReader implements CarLogReader {

	@Override
	public final List<LogEntry> readLog(String filePath, double tpsStartThreshold) throws IOException {
		File logFile = new File(filePath);
		BufferedReader logReader = new BufferedReader(new FileReader(logFile));
		// TODO close reader

		List<String> headers = new ArrayList<>();
		List<String> units = new ArrayList<>();

		String timeColumnKey = null;
		String rpmColumnKey = null;
		String tpsColumnKey = null;

		String logLine;
		List<LogEntry> logEntries = new ArrayList<>();
		while ((logLine = logReader.readLine()) != null) {
			List<String> elements = Arrays.asList((logLine.split("\t")));

			if (elements.size() > 1) {
				if (headers.size() == 0) {
					// headers line
					headers.addAll(elements);
					timeColumnKey = headers.indexOf("Time") != -1 ? "Time" : null;
					rpmColumnKey = headers.indexOf("RPM") != -1 ? "RPM" : null;
					tpsColumnKey = headers.indexOf("TPS") != -1 ? "TPS" : null;

					if (timeColumnKey == null || rpmColumnKey == null || tpsColumnKey == null) {
						throw new IllegalArgumentException("Invalid log file. Cannot find Time, RPM or TPS column.");
					}
				} else if (units.size() == 0) {
					// units line
					units.addAll(elements);
				} else {
					// data line
					LogEntry current = readLogEntry(headers, units, elements, timeColumnKey, rpmColumnKey, tpsColumnKey);
					if (current.getTps().getValue() > tpsStartThreshold) {
						logEntries.add(current);
					}
				}
			}
		}

		// TODO check this
		int maxIdx = logEntries.size();
		for (int i = 1; i < logEntries.size(); i++) {
			if (logEntries.get(i).getRpm().getValue() - logEntries.get(i - 1).getRpm().getValue() < -800) {
				maxIdx = i - 1;
				break;
			}
		}
		if (maxIdx == logEntries.size()) {
			return logEntries;
		}
		return logEntries.subList(0, maxIdx);
	}

	private static LogEntry readLogEntry(List<String> headers, List<String> units, List<String> values,
			String timeColumnKey, String rpmColumnKey, String tpsColumnKey) {

		Map<String, LogValue<Double>> valuesMap = new HashMap<>();
		for (int i = 0; i < 50/* FIXME headers.size()*/; i++) {
			Double value = null;
			try {
				value = Double.valueOf(values.get(i));
			} catch (NumberFormatException e) {
				/*no op*/
			}
			valuesMap.put(headers.get(i), new LogValue<>(value, units.get(i)));
		}
		return new LogEntry(valuesMap, timeColumnKey, rpmColumnKey, tpsColumnKey);
	}

}
