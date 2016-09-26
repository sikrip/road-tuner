package sikrip.roaddyno.web.logger;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import sikrip.roaddyno.eculogreader.MegasquirtLogReader;
import sikrip.roaddyno.gpslogreader.VBOLogReader;
import sikrip.roaddyno.model.InvalidLogFormatException;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.web.model.LogFileData;

/**
 * Responsible to read a log file and create the collection of {@link LogEntry} raw data.
 */
public final class LogFileReader {

	private final static Logger LOGGER = LoggerFactory.getLogger(LogFileReader.class);

	private static final int TPS_START_THRESHOLD = 95;

	private LogFileReader() {
		// no instantiation
	}

	public static LogFileData readLog(MultipartFile file) throws IOException, InvalidLogFormatException {
		if (!file.isEmpty()) {

			// Try megasquirt file
			try {
				return new LogFileData(true, new MegasquirtLogReader().readLog(file.getInputStream(), TPS_START_THRESHOLD));
			} catch (InvalidLogFormatException e) {
				LOGGER.info("File {} is not a valid megasquirt log file.", file.getOriginalFilename());
			}

			// Try vbo file
			try {
				return new LogFileData(false, new VBOLogReader().readLog(file.getInputStream()));
			} catch (InvalidLogFormatException e) {
				LOGGER.info("File {} is not a valid vbo log file.", file.getOriginalFilename());
			}

			// Log file not recognized
			throw new InvalidLogFormatException("Log file is non of the supported types.");
		} else {
			throw new InvalidLogFormatException("Log file is empty.");
		}
	}
}
