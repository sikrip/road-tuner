package sikrip.roaddyno.web.logger;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import sikrip.roaddyno.logreader.MegasquirtLogReader;
import sikrip.roaddyno.logreader.VBOLogReader;
import sikrip.roaddyno.model.InvalidLogFileException;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.web.model.LogFileData;

/**
 * Responsible to read a log file and create the collection of {@link LogEntry} raw data.
 */
public final class LogFileReader {

	private LogFileReader() {
		// no instantiation
	}

	public static LogFileData readLog(MultipartFile file) throws InvalidLogFileException {
		if (!file.isEmpty()) {
			try {
				final String originalFileName = file.getOriginalFilename().toLowerCase();
				if (originalFileName.endsWith("msl")) {
					// megasquirt file
					return new LogFileData(true, new MegasquirtLogReader().readLog(file.getInputStream()));
				} else if (originalFileName.endsWith("vbo")) {
					// vbo file
					return new LogFileData(false, new VBOLogReader().readLog(file.getInputStream()));
				}
				throw new InvalidLogFileException("Unknown or not supported log file");
			} catch (IOException e) {
				throw new InvalidLogFileException("Could not read log file");
			}
		} else {
			throw new InvalidLogFileException("Log file is empty.");
		}
	}
}
