package sikrip.roaddyno.web.model;

import sikrip.roaddyno.model.LogValue;
import sikrip.roaddyno.model.WotRunBounds;
import sikrip.roaddyno.engine.DynoRunDetector;
import sikrip.roaddyno.model.LogEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains the log file data along with possible WOT runs within the log data.
 */
public final class RunData {

    /**
     * True for log files that are RPM based (usually ECU logs)
     * false for speed based log files (usually gps log files).
     */
    private final boolean rpmBased;

    /**
     * The raw entries produced after reading a log file.
     */
    private final List<LogEntry> logEntries;

    /**
     * The bounds(indices within the {@link #logEntries}) of the possible WOT runs.
     */
    private final List<WotRunBounds> wotRunBounds = new ArrayList<>();

    public RunData(boolean rpmBased, List<LogEntry> logEntries) {
        this.rpmBased = rpmBased;
        this.logEntries = logEntries;
        this.wotRunBounds.addAll(findAccelerationRuns(rpmBased, logEntries));
    }

    /**
     * Finds the possible WOT runs on the provided log entries.
     *
     * @param rpmBased   true if the log entries are RPM based, false otherwise
     * @param logEntries the raw data
     *
     * @return a list of possible WOT runs
     */
    private List<WotRunBounds> findAccelerationRuns(boolean rpmBased, List<LogEntry> logEntries) {
        if (rpmBased) {
            return DynoRunDetector.getWotRunBoundsByRPM(logEntries);
        }
        return DynoRunDetector.getWotRunBoundsBySpeed(logEntries);
    }

    public boolean isRpmBased() {
        return rpmBased;
    }

    public List<LogEntry> getLogEntries() {
        return logEntries;
    }

    public List<WotRunBounds> getWotRunBounds() {
        return wotRunBounds;
    }
}
