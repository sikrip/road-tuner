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
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.LogValue;

public class AuxiliaryChartDataProvider {

	public static final String RPM_AXIS = "rpm";

	private Map<String, Object> root = new HashMap<>();
	private DecimalFormat df = new DecimalFormat("#.0");

	public AuxiliaryChartDataProvider() {
		df.setRoundingMode(RoundingMode.DOWN);
	}

	public Map<String, Object> createJsonData(List<UploadedRun> runs, String field) {
		root.clear();

		root.put("type", "xy");
		root.put("startDuration", 1);

		root.put("trendLines", new ArrayList<>());

		createGraphDefinitions(runs, field);

		root.put("guides", new ArrayList<>());

		createValueAxisDefinition();

		root.put("allLabels", new ArrayList<>());
		root.put("balloon", new HashMap<>());

		createLegendDefinition();

		createTitlesDefinition();

		createDataDefinition(runs, field);

		return root;
	}

	private void createGraphDefinitions(List<UploadedRun> runs, String field) {

		List<Map<String, Object>> graphs = new ArrayList<>();

		for (int iRun = 0; iRun < runs.size(); iRun++) {

			UploadedRun run = runs.get(iRun);

			DynoSimulationResult simulationResult = run.getResult();

			String runColor = run.getColor();

			DynoSimulationEntry maxPower = simulationResult.maxPower();
			DynoSimulationEntry maxTorque = simulationResult.maxTorque();

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
				LogValue<?> logValue = runs.get(iRun).getResult().getAt(rpm, field);
				if (logValue != null) {
					dataEntry.put(field  + iRun, df.format(logValue.getValue()));
				}
			}
			dataProvider.add(dataEntry);
		}
		root.put("dataProvider", dataProvider);
	}

	private List<Double> getRpmValuesUnion(List<UploadedRun> simulationResults) {
		List<Double> rpmValues = new ArrayList<>();
		for (UploadedRun simulationResult : simulationResults) {
			for (double rpm : simulationResult.getResult().powerDataset()[0]) {//FIXME getSmoothedRpm?
				rpmValues.add(rpm);
			}
		}
		Collections.sort(rpmValues);
		return rpmValues;
	}

	private void createTitlesDefinition() {
		List<Map<String, Object>> titles = new ArrayList<>();
		Map<String, Object> title = new HashMap<>();
		title.put("id", "Title-1");
		title.put("size", 15);
		title.put("text", "Road Dyno Plot");
		titles.add(title);
		root.put("titles", titles);
	}

	private void createLegendDefinition() {
		Map<String, Boolean> legend = new HashMap<>();
		legend.put("enabled", true);
		legend.put("useGraphSettings", true);
		root.put("legend", legend);
	}

	private void createValueAxisDefinition() {
		List<Map<String, String>> valueAxes = new ArrayList<>();

		Map<String, String> valueAxis = new HashMap<>();

		valueAxis.put("id", "Y-axis");
		valueAxis.put("position", "left");
		valueAxis.put("title", "Hp | lb/ft");
		valueAxes.add(valueAxis);

		valueAxis = new HashMap<>();
		valueAxis.put("id", "X-axis");
		valueAxis.put("position", "bottom");
		valueAxis.put("title", "RPM");
		valueAxes.add(valueAxis);

		root.put("valueAxes", valueAxes);
	}

}
