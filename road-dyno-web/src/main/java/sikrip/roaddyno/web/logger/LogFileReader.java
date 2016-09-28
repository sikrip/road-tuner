package sikrip.roaddyno.web.logger;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import sikrip.roaddyno.eculogreader.MegasquirtLogReader;
import sikrip.roaddyno.gpslogreader.VBOLogReader;
import sikrip.roaddyno.model.InvalidLogFileException;
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

	public static LogFileData readLog(MultipartFile file) throws InvalidLogFileException {
		if (!file.isEmpty()) {
			try {
				final String originalFileName = file.getOriginalFilename().toLowerCase();
				if (originalFileName.endsWith("msl")) {
					// megasquirt file
					return new LogFileData(true, new MegasquirtLogReader().readLog(file.getInputStream(), TPS_START_THRESHOLD));
				} else if (originalFileName.endsWith("vbo")) {
					// vbo file
					return new LogFileData(false, new VBOLogReader().readLog(file.getInputStream()));
				}
				throw new InvalidLogFileException("Unknown / not supported log file");
			} catch (IOException e) {
				throw new InvalidLogFileException("Could not read log file");
			}
		} else {
			throw new InvalidLogFileException("Log file is empty.");
		}
	}
}
