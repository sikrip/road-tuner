package sikrip.roaddyno.engine

import sikrip.roaddyno.logreader.MegasquirtLogReader
import sikrip.roaddyno.logreader.VBOLogReader
import sikrip.roaddyno.model.LogEntry
import sikrip.roaddyno.model.WotRunBounds
import spock.lang.Specification
import spock.lang.Unroll

class WotRunDetectorTest extends Specification {

    def "Verify Megasquirt file"() {
        given: "WOT identification on a megasquirt log file"
        MegasquirtLogReader logReader = new MegasquirtLogReader();

        List<LogEntry> logEntries = logReader.readLog(this.getClass().getResource("/sample-dyno-run.msl").getPath());
        List<WotRunBounds> wotRunBounds = WotRunDetector.getWotRunBounds(true, logEntries);
        expect: "one WOT run"
        wotRunBounds.size() == 1
        and: "with the proper start index"
        wotRunBounds.get(0).getStart() == 464
        and: "and the proper end index"
        wotRunBounds.get(0).getEnd() == 602
    }

    def "Verify VBO file"() {
        given: "WOT identification on a megasquirt log file"
        VBOLogReader logReader = new VBOLogReader();

        List<LogEntry> logEntries = logReader.readLog(this.getClass().getResource("/sample-vbo.vbo").getPath());
        List<WotRunBounds> wotRunBounds = WotRunDetector.getWotRunBounds(false, logEntries)

        expect: "one WOT run"
        wotRunBounds.size() == 1
        and: "with the proper start index"
        wotRunBounds.get(0).getStart() == 0
        and: "and the proper end index"
        wotRunBounds.get(0).getEnd() == 48
    }

    @Unroll
    def "Log file with multiple accelerations to have WOT run from position #startIdx to #endIdx"(int startIdx, int endIdx) {
        given: "WOT detection of the log file"
        VBOLogReader logReader = new VBOLogReader();

        List<LogEntry> logEntries = logReader.readLog(this.getClass().getResource("/sample-vbo-1.vbo").getPath());
        List<WotRunBounds> wotRunBounds = WotRunDetector.getWotRunBounds(false, logEntries)
        expect: "WOT from $startIdx to $endIdx"
        wotRunBounds.find({it.start == startIdx}).end == endIdx
        where:
        startIdx | endIdx
        520	    |	562
        1046	|	1095
        1125	|	1165
        1212	|	1238
        1261	|	1320
        1355	|	1408
        1454	|	1500
        1522	|	1567
        1600	|	1625
        1634	|	1694
        1719	|	1770
        1812	|	1860
        1880	|	1924
        1953	|	1978
        1997	|	2046
        2074	|	2123
        2160	|	2206
        2224	|	2270
        2347	|	2410
        2434	|	2483
        2521	|	2569
        2585	|	2631
        2661	|	2687
        2701	|	2756
        2780	|	2829
        3120	|	3187
        3214	|	3227
        3228	|	3261
        3299	|	3348
        3367	|	3414
        3442	|	3469
        3479	|	3536
        3563	|	3611
        3664	|	3705
    }

    @Unroll
    def "Log file with no deceleration at the end to have WOT run from position #startIdx to #endIdx"(int startIdx, int endIdx) {
        given: "WOT detection of the log file"
        VBOLogReader logReader = new VBOLogReader();

        List<LogEntry> logEntries = logReader.readLog(this.getClass().getResource("/vbo-no-deceleration-at-the-end.vbo").getPath());
        List<WotRunBounds> wotRunBounds = WotRunDetector.getWotRunBounds(false, logEntries)
        expect: "WOT from $startIdx to $endIdx"
        wotRunBounds.find({it.start == startIdx}).end == endIdx
        where:
        startIdx | endIdx
        1618	|	1668
        2307	|	2355
        2468	|	2537
        2601	|	2665
        2746	|	2786
        2824	|	2875
        2877	|	2924
        2990	|	3052
    }

}
