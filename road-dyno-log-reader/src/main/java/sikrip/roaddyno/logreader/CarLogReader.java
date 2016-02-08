package sikrip.roaddyno.logreader;

import java.io.IOException;
import java.util.List;

import sikrip.roaddyno.model.LogEntry;


public interface CarLogReader {

    List<LogEntry> readLog(String file, double tpsStartThreshold) throws IOException;
}
