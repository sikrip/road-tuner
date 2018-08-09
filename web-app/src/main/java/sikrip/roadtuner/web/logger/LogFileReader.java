package sikrip.roadtuner.web.logger;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import sikrip.roaddyno.logreader.DatalogitLogReader;
import sikrip.roaddyno.logreader.MegasquirtLogReader;
import sikrip.roaddyno.logreader.VBOLogReader;
import sikrip.roaddyno.model.InvalidLogFileException;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.RunData;

import static sikrip.roaddyno.engine.WotRunDetector.getWotRunBounds;

/**
 * Responsible to read a log file and create the collection of {@link LogEntry} raw data.
 */
public final class LogFileReader {

	private LogFileReader() {
		// no instantiation
	}

	public static RunData readLog(MultipartFile file) throws InvalidLogFileException {
		if (!file.isEmpty()) {
			try {
				boolean rpmBased;
				List<LogEntry> logEntries;
				final String originalFileName = file.getOriginalFilename().toLowerCase();
				if (originalFileName.endsWith("msl")) {
					// megasquirt file
					logEntries = new MegasquirtLogReader().readLog(file.getInputStream());
					rpmBased = true;
				} else if (originalFileName.endsWith("vbo")) {
					// vbo file
					rpmBased = false;
					logEntries = new VBOLogReader().readLog(file.getInputStream());
				} else if (originalFileName.endsWith("txt")) {
					// PFC/Datalogit file
					rpmBased = true;
					logEntries = new DatalogitLogReader().readLog(file.getInputStream());
				} else {
					throw new InvalidLogFileException("Unknown or not supported log file");
				}
				final RunData runData = new RunData(rpmBased, file.getName(), logEntries);
				runData.setWotRunBounds(getWotRunBounds(rpmBased, logEntries));

				return runData;
			} catch (IOException e) {
				throw new InvalidLogFileException("Could not read log file");
			}
		} else {
			throw new InvalidLogFileException("Log file is empty.");
		}
	}
}
