package sikrip.roaddyno.model;

import java.util.List;

public final class LogFileData {

	private final boolean rpmBased;
	private final List<LogEntry> logEntries;

	public LogFileData(boolean rpmBased, List<LogEntry> logEntries) {
		this.rpmBased = rpmBased;
		this.logEntries = logEntries;
	}

	public boolean isRpmBased() {
		return rpmBased;
	}

	public List<LogEntry> getLogEntries() {
		return logEntries;
	}
}
