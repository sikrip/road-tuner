package sikrip.roaddyno.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

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

    private final String name;

    /**
     * The bounds(indices within the {@link #logEntries}) of the possible WOT runs.
     */
    private final List<WotRunBounds> wotRunBounds = new ArrayList<>();

    public RunData(boolean rpmBased, String name, List<LogEntry> logEntries) {
        this.rpmBased = rpmBased;
        this.name = name;
        this.logEntries = logEntries;
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

    public void setWotRunBounds(List<WotRunBounds> wotRunBounds) {
        this.wotRunBounds.clear();
        this.wotRunBounds.addAll(wotRunBounds);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "RunData{" +
                "name='" + name + '\'' +
                '}';
    }
}
