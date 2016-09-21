package sikrip.roaddyno.engine;

import java.util.List;

import org.junit.Test;

import sikrip.roaddyno.gpslogreader.GPSLogReader;
import sikrip.roaddyno.gpslogreader.VBOLogReader;
import sikrip.roaddyno.model.DynoSimulationResult;
import sikrip.roaddyno.model.LogEntry;

public class SpeedDynoSimulatorTests {

	@Test
	public void verify() throws Exception {
		double fgr = 4.3;
		double gr = 1.310;
		double tyreDiameter = 590;
		double carWeight = 920;
		double occWeight = 90;
		double fa = 1.7;
		double cd = 0.33;

		GPSLogReader logReader = new VBOLogReader();

		List<LogEntry> logEntries = logReader.readLog(DynoRunDetectorTest.getTestResourceUrl("/sample-vbo-1.vbo").getPath());

		List<LogEntry> runLogEntries = logEntries.subList(3479, 3536);

		for (LogEntry runLogEntry : runLogEntries) {
			System.out.println(runLogEntry.get("height"));
		}
		DynoSimulationResult result = DynoSimulator.runBySpeed(runLogEntries, fgr, gr, tyreDiameter, carWeight, occWeight, fa, cd);

		double[][] dataset = result.powerDataset();

		for(int i=0; i< result.getEntriesSize(); i++){
			System.out.println(dataset[0][i]+": "+dataset[1][i]);
		}

	}
}
