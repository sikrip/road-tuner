package sikrip.roaddyno.logreader;

import java.io.IOException;
import java.util.List;

import sikrip.roaddyno.model.LogEntry;

/**
 * API for readers of log files produced by ECUs.
 */
public interface EcuLogReader {

	List<LogEntry> readLog(String file, double tpsStartThreshold) throws IOException;

}