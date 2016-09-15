package sikrip.roaddyno.gpslogreader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import sikrip.roaddyno.model.LogEntry;

public interface GPSLogReader {

	List<LogEntry> readLog(String filePath) throws IOException;


	List<LogEntry> readLog(InputStream inputStream) throws IOException;
}
