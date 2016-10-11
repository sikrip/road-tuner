package sikrip.roaddyno.web.chart;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sikrip.roaddyno.engine.DynoSimulationEntry;
import sikrip.roaddyno.engine.DynoSimulationResult;
import sikrip.roaddyno.model.LogValue;
import sikrip.roaddyno.web.model.LoggedRunsEntry;

public final class ChartDataProvider {

	public static final String RPM_AXIS = "rpm";
	public static final String POWER_FIELD = "power_";
	public static final String TORQUE_FIELD = "torque_";

	private final Map<String, Object> root = new HashMap<>();
	private final DecimalFormat df = new DecimalFormat("#.0");

	public ChartDataProvider() {
		df.setRoundingMode(RoundingMode.DOWN);
	}

	public Map<String, Object> createMainChartDefinition(List<LoggedRunsEntry> runs) {
		root.clear();

		root.put("type", "xy");
		root.put("startDuration", 1);

		createExportDefinition(runs);

		createMaxPowerAndTorqueLabels(runs);

		root.put("trendLines", new ArrayList<>());

		createGraphDefinitions(runs);

		root.put("guides", new ArrayList<>());

		createAxesDefinitions("Hp | lb/ft", runs);

		root.put("balloon", new HashMap<>());

		createLegendDefinition();

		createTitlesDefinition("Wheel power / torque");

		createDataDefinition(runs);

		return root;
	}

	private void createMaxPowerAndTorqueLabels(List<LoggedRunsEntry> runs) {
		final List<Map<String, Object>> labelDefinitions = new ArrayList<>();

		int y = 70;
		for (LoggedRunsEntry run : runs) {

			final Map<String, Object> labelDef = new HashMap<>();
			DynoSimulationEntry maxPower = run.getMaxPower();
			DynoSimulationEntry maxTorque = run.getMaxTorque();
			labelDef.put("text", String.format("%s %.1fHP @%.0fRPM, %.1flb/ft @%.0fRPM",
					run.getName(), maxPower.getPower(), maxPower.getRpm(), maxTorque.getTorque(), maxTorque.getRpm()));
			labelDef.put("bold", false);
			labelDef.put("size", 14);
			labelDef.put("color", run.getColor());
			labelDef.put("x", 70);
			labelDef.put("y", y);

			labelDefinitions.add(labelDef);

			y += 15;
		}
		root.put("allLabels", labelDefinitions);
	}

	private void createExportDefinition(List<LoggedRunsEntry> runs) {

		final StringBuilder graphNameBuilder = new StringBuilder();
		for (int i = 0; i < runs.size(); i++) {
			graphNameBuilder.append(runs.get(i).getName());
			if (i < runs.size() - 1) {
				graphNameBuilder.append("-VS-");
			}
		}
		final String graphName = graphNameBuilder.toString();

		final List<Map<String, Object>> downloadOptions = new ArrayList<>();

		// PNG
		Map<String, Object> menuSettings = new HashMap<>();
		menuSettings.put("format", "PNG");
		menuSettings.put("label", "Save as PNG");
		menuSettings.put("title", "Export dyno plots to PNG");
		menuSettings.put("fileName", graphName);
		downloadOptions.add(menuSettings);

		// JPG
		menuSettings = new HashMap<>();
		menuSettings.put("format", "JPG");
		menuSettings.put("label", "Save as JPG");
		menuSettings.put("title", "Export dyno plots to JPG");
		menuSettings.put("fileName", graphName);
		downloadOptions.add(menuSettings);

		// PDF
		menuSettings = new HashMap<>();
		menuSettings.put("format", "PDF");
		menuSettings.put("label", "Save as PDF");
		menuSettings.put("title", "Export dyno plots to PDF");
		menuSettings.put("fileName", graphName);
		downloadOptions.add(menuSettings);

		final Map<String, Object> firstLevelMenu = new HashMap<>();

		firstLevelMenu.put("class", "export-main");

		firstLevelMenu.put("menu", downloadOptions);

		final List<Object> firstLeventMenus = new ArrayList<>();
		firstLeventMenus.add(firstLevelMenu);

		final Map<String, Object> rootMenu = new HashMap<>();
		rootMenu.put("enabled", true);
		rootMenu.put("menu", firstLeventMenus);

		root.put("export", rootMenu);
	}

	public Map<String, Object> createAuxiliaryChartDefinition(List<LoggedRunsEntry> runs, String field) {
		root.clear();

		root.put("type", "xy");
		root.put("startDuration", 1);

		root.put("trendLines", new ArrayList<>());

		createGraphDefinitions(runs, field);

		root.put("guides", new ArrayList<>());

		createAxesDefinitions(field, null);

		root.put("allLabels", new ArrayList<>());
		root.put("balloon", new HashMap<>());

		createTitlesDefinition(field);

		createDataDefinition(runs, field);

		return root;
	}

	private void createGraphDefinitions(List<LoggedRunsEntry> runs) {

		List<Map<String, Object>> graphs = new ArrayList<>();

		for (int iRun = 0; iRun < runs.size(); iRun++) {

			LoggedRunsEntry run = runs.get(iRun);

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

	private void createDataDefinition(List<LoggedRunsEntry> runs) {
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
					dataEntry.put(POWER_FIELD + iRun, power);
					dataEntry.put(TORQUE_FIELD + iRun, torque);
				}

			}
			dataProvider.add(dataEntry);
		}

		root.put("dataProvider", dataProvider);
	}

	private void createGraphDefinitions(List<LoggedRunsEntry> runs, String field) {

		List<Map<String, Object>> graphs = new ArrayList<>();

		for (int iRun = 0; iRun < runs.size(); iRun++) {

			LoggedRunsEntry run = runs.get(iRun);

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

	private void createDataDefinition(List<LoggedRunsEntry> runs, String field) {

		List<Map<String, Object>> dataProvider = new ArrayList<>();

		List<Double> rpmValues = getRpmValuesUnion(runs);
		for (Double rpm : rpmValues) {

			Map<String, Object> dataEntry = new HashMap<>();
			dataEntry.put(RPM_AXIS, rpm);

			for (int iRun = 0; iRun < runs.size(); iRun++) {
				LogValue<?> logValue = runs.get(iRun).getResult().getLogEntryAt(rpm, field);
				if (logValue != null) {
					dataEntry.put(field + iRun, df.format(logValue.getValue()));
				}
			}
			dataProvider.add(dataEntry);
		}
		root.put("dataProvider", dataProvider);
	}

	private List<Double> getRpmValuesUnion(List<LoggedRunsEntry> runs) {
		List<Double> rpmValues = new ArrayList<>();
		for (LoggedRunsEntry run : runs) {
			for (int i = 0; i < run.getResult().getEntriesSize(); i++) {
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

	private void createAxesDefinitions(String yTitle, List<LoggedRunsEntry> runs) {

		Double maxYVal = null;

		if (runs != null) {
			maxYVal = 0.0;
			for (LoggedRunsEntry run : runs) {
				DynoSimulationEntry maxPower = run.getMaxPower();
				DynoSimulationEntry maxTorque = run.getMaxTorque();
				if (Math.max(maxPower.getPower(), maxTorque.getTorque()) > maxYVal) {
					maxYVal = Math.max(maxPower.getPower(), maxTorque.getTorque());
				}
			}
		}

		List<Map<String, Object>> valueAxes = new ArrayList<>();

		Map<String, Object> valueAxis = new HashMap<>();

		valueAxis.put("id", "Y-axis");
		valueAxis.put("position", "left");
		if (maxYVal != null) {
			valueAxis.put("maximum", maxYVal + 15 * runs.size());
		}
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
