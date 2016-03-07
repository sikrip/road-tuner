package sikrip.roaddyno.web.chart;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sikrip.roaddyno.model.DynoSimulationEntry;
import sikrip.roaddyno.model.DynoSimulationResult;
import sikrip.roaddyno.model.LogValue;

public class ChartDataProvider {

	public static final String RPM_AXIS = "rpm";
	public static final String POWER_FIELD = "power_";
	public static final String TORQUE_FIELD = "torque_";

	private Map<String, Object> root = new HashMap<>();
	private DecimalFormat df = new DecimalFormat("#.0");

	public ChartDataProvider() {
		df.setRoundingMode(RoundingMode.DOWN);
	}

	public Map<String, Object> createMainChartDefinition(List<UploadedRun> runs) {
		root.clear();

		root.put("type", "xy");
		root.put("startDuration", 1);

		root.put("trendLines", new ArrayList<>());

		createGraphDefinitions(runs);

		root.put("guides", new ArrayList<>());

		createAxesDefinitions("Hp | lb/ft");

		root.put("allLabels", new ArrayList<>());
		root.put("balloon", new HashMap<>());

		createLegendDefinition();

		createTitlesDefinition("Wheel power / torque");

		createDataDefinition(runs);

		return root;
	}

	public Map<String, Object> createAuxuliaryChartDefinition(List<UploadedRun> runs, String field) {
		root.clear();

		root.put("type", "xy");
		root.put("startDuration", 1);

		root.put("trendLines", new ArrayList<>());

		createGraphDefinitions(runs, field);

		root.put("guides", new ArrayList<>());

		createAxesDefinitions(field);

		root.put("allLabels", new ArrayList<>());
		root.put("balloon", new HashMap<>());

		createTitlesDefinition(field);

		createDataDefinition(runs, field);

		return root;
	}

	private void createGraphDefinitions(List<UploadedRun> runs) {

		List<Map<String, Object>> graphs = new ArrayList<>();

		for (int iRun = 0; iRun < runs.size(); iRun++) {

			UploadedRun run = runs.get(iRun);

			DynoSimulationResult simulationResult = run.getResult();

			String runColor = run.getColor();

			DynoSimulationEntry maxPower = simulationResult.maxPower();
			DynoSimulationEntry maxTorque = simulationResult.maxTorque();

			Map<String, Object> graph = new HashMap<>();
			graph.put("balloonText", "[[title]] [[value]] @[[category]]");//FIXME

			//graph.put("bullet", "round");
			//graph.put("bulletSize", 3);

			graph.put("id", "Graph-" + POWER_FIELD + iRun);
			graph.put("lineColor", runColor);
			graph.put("lineThickness", 3);
			graph.put("title", run.getName() + " power");
			graph.put("type", "smoothedLine");
			graph.put("xField", RPM_AXIS);
			graph.put("yField", POWER_FIELD + iRun);
			graphs.add(graph);

			graph = new HashMap<>();
			graph.put("balloonText", "[[title]] [[value]] @[[category]]");

			//graph.put("bullet", "square");

			graph.put("id", "Graph-" + TORQUE_FIELD + iRun);
			graph.put("dashLength", 4);
			graph.put("lineColor", runColor);
			graph.put("lineThickness", 2);
			graph.put("title", run.getName() + " torque");
			graph.put("type", "smoothedLine");
			graph.put("xField", RPM_AXIS);
			graph.put("yField", TORQUE_FIELD + iRun);
			graphs.add(graph);
		}

		root.put("graphs", graphs);
	}

	private void createDataDefinition(List<UploadedRun> runs) {
		List<Map<String, Object>> dataProvider = new ArrayList<>();

		List<Double> rpmValues = getRpmValuesUnion(runs);

		for (Double rpm : rpmValues) {

			Map<String, Object> dataEntry = new HashMap<>();
			dataEntry.put(RPM_AXIS, rpm);

			for (int iRun = 0; iRun < runs.size(); iRun++) {

				DynoSimulationResult simulationResult = runs.get(iRun).getResult();

				DynoSimulationEntry simulationEntry = simulationResult.getResultAt(rpm);

				if (simulationEntry != null) {
					String power = df.format(simulationEntry.getPower());
					String torque = df.format(simulationEntry.getTorque());
					dataEntry.put(POWER_FIELD  + iRun, power);
					dataEntry.put(TORQUE_FIELD  + iRun, torque);
				}

			}
			dataProvider.add(dataEntry);
		}

		root.put("dataProvider", dataProvider);
	}

	private void createGraphDefinitions(List<UploadedRun> runs, String field) {

		List<Map<String, Object>> graphs = new ArrayList<>();

		for (int iRun = 0; iRun < runs.size(); iRun++) {

			UploadedRun run = runs.get(iRun);

			String runColor = run.getColor();

			Map<String, Object> graph = new HashMap<>();
			graph.put("balloonText", "[[title]] [[value]] @[[category]]");

			//graph.put("bullet", "round");
			//graph.put("bulletSize", 3);

			graph.put("id", "Graph-" + field + iRun);
			graph.put("lineColor", runColor);
			graph.put("lineThickness", 3);
			graph.put("type", "smoothedLine");
			graph.put("xField", RPM_AXIS);
			graph.put("yField", field + iRun);
			graphs.add(graph);
		}

		root.put("graphs", graphs);
	}

	private void createDataDefinition(List<UploadedRun> runs, String field) {

		List<Map<String, Object>> dataProvider = new ArrayList<>();

		List<Double> rpmValues = getRpmValuesUnion(runs);
		for (Double rpm : rpmValues) {

			Map<String, Object> dataEntry = new HashMap<>();
			dataEntry.put(RPM_AXIS, rpm);

			for (int iRun = 0; iRun < runs.size(); iRun++) {
				LogValue<?> logValue = runs.get(iRun).getResult().getLogEntryAt(rpm, field);
				if (logValue != null) {
					dataEntry.put(field  + iRun, df.format(logValue.getValue()));
				}
			}
			dataProvider.add(dataEntry);
		}
		root.put("dataProvider", dataProvider);
	}

	private List<Double> getRpmValuesUnion(List<UploadedRun> runs) {
		List<Double> rpmValues = new ArrayList<>();
		for (UploadedRun run : runs) {
			for (int i=0; i< run.getResult().getEntriesSize(); i++) {
				rpmValues.add(run.getResult().getSmoothedRpmAt(i));
			}
		}
		Collections.sort(rpmValues);
		return rpmValues;
	}

	private void createTitlesDefinition(String title) {
		List<Map<String, Object>> titles = new ArrayList<>();
		Map<String, Object> titleDef = new HashMap<>();
		titleDef.put("id", "Title-1");
		titleDef.put("size", 15);
		titleDef.put("text", title);
		titles.add(titleDef);
		root.put("titles", titles);
	}

	private void createLegendDefinition() {
		Map<String, Boolean> legend = new HashMap<>();
		legend.put("enabled", true);
		legend.put("useGraphSettings", true);
		root.put("legend", legend);
	}

	private void createAxesDefinitions(String yTitle) {
		List<Map<String, String>> valueAxes = new ArrayList<>();

		Map<String, String> valueAxis = new HashMap<>();

		valueAxis.put("id", "Y-axis");
		valueAxis.put("position", "left");
		valueAxis.put("title", yTitle);
		valueAxes.add(valueAxis);

		valueAxis = new HashMap<>();
		valueAxis.put("id", "X-axis");
		valueAxis.put("position", "bottom");
		valueAxis.put("title", "RPM");
		valueAxes.add(valueAxis);

		root.put("valueAxes", valueAxes);
	}

}
