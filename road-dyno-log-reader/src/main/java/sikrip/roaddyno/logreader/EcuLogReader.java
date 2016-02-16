package sikrip.roaddyno.logreader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import sikrip.roaddyno.model.LogEntry;

/**
 * API for readers of log files produced by ECUs.
 */
public interface EcuLogReader {

	/**
	 * Reads the log of the provided file.
	 *
	 * @param file
	 * 		the log file
	 * @param tpsStartThreshold
	 * 		the threshold of TPS value above which the reader should start reading
	 * @return a list of log entries
	 * @throws IOException
	 * 		if reading the file goes wrong
	 * @throws InvalidLogFormatException
	 * 		if the log format is invalid
	 */
	List<LogEntry> readLog(String file, double tpsStartThreshold) throws IOException, InvalidLogFormatException;

	/**
	 * Reads the log from the provided stream.
	 *
	 * @param inputStream
	 * 		the stream
	 * @param tpsStartThreshold
	 * 		the threshold of TPS value above which the reader should start reading
	 * @return a list of log entries
	 * @throws IOException
	 * 		if reading the file goes wrong
	 * @throws InvalidLogFormatException
	 * 		if the log format is invalid
	 */
	List<LogEntry> readLog(InputStream inputStream, double tpsStartThreshold) throws IOException, InvalidLogFormatException;

}