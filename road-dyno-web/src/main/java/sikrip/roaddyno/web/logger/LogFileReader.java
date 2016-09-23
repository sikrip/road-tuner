package sikrip.roaddyno.web.logger;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import sikrip.roaddyno.eculogreader.MegasquirtLogReader;
import sikrip.roaddyno.gpslogreader.VBOLogReader;
import sikrip.roaddyno.model.InvalidLogFormatException;
import sikrip.roaddyno.model.LogEntry;

public final class LogFileReader {

	private final static Logger LOGGER = LoggerFactory.getLogger(LogFileReader.class);

	private static final int TPS_START_THRESHOLD = 95;

	private LogFileReader() {
		// no instantiation
	}

	public static LogFileData readLog(MultipartFile file) throws IOException, InvalidLogFormatException {
		if (!file.isEmpty()) {
			List<LogEntry> logEntries = null;
			boolean rpmBased = false;

			try {
				// try megasquirt file
				logEntries = new MegasquirtLogReader().readLog(file.getInputStream(), TPS_START_THRESHOLD);
				rpmBased = true;
			} catch (InvalidLogFormatException e) {
				// will try other log file types
				LOGGER.info("File {} is not a valid megasquirt log file", file.getName());
			}

			try {
				// try vbo file
				logEntries = new VBOLogReader().readLog(file.getInputStream());
				rpmBased = false;
			} catch (InvalidLogFormatException e) {
				LOGGER.info("File {} is not a valid vbo log file", file.getOriginalFilename());
			}
			if (logEntries == null) {
				throw new IllegalArgumentException("Could not read file, unknown format.");
			} else {
				return new LogFileData(rpmBased, logEntries);
			}
		} else {
			throw new InvalidLogFormatException("Log file is empty");
		}
	}
}
