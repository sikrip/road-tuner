package sikrip.roadtuner.web.chart;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sikrip.roadtuner.model.DynoSimulationEntry;
import sikrip.roadtuner.model.DynoSimulationResult;
import sikrip.roadtuner.model.LogValue;
import sikrip.roadtuner.web.model.RunPlot;

/**
 * Responsible to provide the definitions of the dyno plots.
 */
public final class ChartDataProvider {

	private static final String RPM_AXIS = "rpm";
	private static final String POWER_FIELD = "power_";
	private static final String TORQUE_FIELD = "torque_";

	private final Map<String, Object> root = new HashMap<>();
	private final DecimalFormat valuesFormat = new DecimalFormat("#.0");

	public ChartDataProvider() {
		valuesFormat.setRoundingMode(RoundingMode.DOWN);
	}

	/**
	 * Creates the definition for the main dyno plot(power/torque)
	 *
	 * @param runs
	 * 		the dyno runs
	 * @return a map with the definitions
	 */
	public Map<String, Object> createMainChartDefinition(List<RunPlot> runs) {
		root.clear();

		root.put("type", "xy");
		root.put("startDuration", 1);

		createExportDefinition(runs);
		createMaxPowerAndTorqueLabels(runs);
		createGraphDefinitions(runs);
		createAxesDefinitions("Hp | lb/ft", runs);
		createLegendDefinition();
		createTitlesDefinition("Wheel power / torque");
		createMainDataDefinition(runs);

		// TODO add trendLines root.put("trendLines", new ArrayList<>());
		// TODO add guides root.put("guides", new ArrayList<>());
		// TODO add balloon root.put("balloon", new HashMap<>());

		return root;
	}

	/**
	 * Creates the definition of the plot for the provided field.
	 *
	 * @param runs
	 * 		the dyno runs
	 * @param field
	 * 		the field to plot
	 * @return a map with the definitions
	 */
	public Map<String, Object> createAuxiliaryChartDefinition(List<RunPlot> runs, String field) {
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

		createAuxDataDefinition(runs, field);

		return root;
	}

	private void createMaxPowerAndTorqueLabels(List<RunPlot> runs) {
		final List<Map<String, Object>> labelDefinitions = new ArrayList<>();

		int y = 70;
		for (RunPlot run : runs) {

			final Map<String, Object> labelDef = new HashMap<>();
			DynoSimulationEntry maxPower = run.getMaxPower();
			DynoSimulationEntry maxTorque = run.getMaxTorque();
			labelDef.put(
				"text",
				String.format(
					"%s %.1fHP @%.0fRPM, %.1flb/ft(%.1fNm) @%.0fRPM",
					run.getName(),
					maxPower.getPower(),
					maxPower.getRpm(),
					maxTorque.getTorque(),
					maxTorque.getTorque() * 1.35581795,
					maxTorque.getRpm()
				)
			);
			labelDef.put("bold", false);
			labelDef.put("size", 14);
			labelDef.put("color", run.getColor());
			labelDef.put("x", 75);
			labelDef.put("y", y);

			labelDefinitions.add(labelDef);

			y += 15;
		}
		root.put("allLabels", labelDefinitions);
	}

	private void createExportDefinition(List<RunPlot> runs) {

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

	private void createGraphDefinitions(List<RunPlot> runs) {

		final List<Map<String, Object>> graphs = new ArrayList<>();

		for (int iRun = 0; iRun < runs.size(); iRun++) {

			RunPlot run = runs.get(iRun);

			Map<String, Object> graph = new HashMap<>();

			// Power graph
			graph.put("id", "Graph-" + POWER_FIELD + iRun);
			graph.put("lineColor", run.getColor());
			graph.put("lineThickness", 1);
			graph.put("title", run.getName() + " power");
			graph.put("type", "smoothedLine");
			graph.put("xField", RPM_AXIS);
			graph.put("yField", POWER_FIELD + iRun);

			graph.put("bullet", "round");
			graph.put("bulletSize", 4);
			graph.put("balloonText", "RPM:[[" + RPM_AXIS +"]]" + " Power: [["+ POWER_FIELD + iRun +"]]");
			graphs.add(graph);

			// Torque graph
			graph = new HashMap<>();
			graph.put("id", "Graph-" + TORQUE_FIELD + iRun);
			graph.put("dashLength", 4);
			graph.put("lineColor", run.getColor());
			graph.put("lineThickness", 2);
			graph.put("title", run.getName() + " torque");
			graph.put("type", "smoothedLine");
			graph.put("xField", RPM_AXIS);
			graph.put("yField", TORQUE_FIELD + iRun);
			graphs.add(graph);
		}

		root.put("graphs", graphs);
	}

	private void createMainDataDefinition(List<RunPlot> runs) {
		final List<Map<String, Object>> dataProvider = new ArrayList<>();
		final List<Double> rpmValues = getRpmValuesUnion(runs);
		for (Double rpm : rpmValues) {

			final Map<String, Object> dataEntry = new HashMap<>();
			dataEntry.put(RPM_AXIS, rpm.intValue());

			for (int iRun = 0; iRun < runs.size(); iRun++) {

				DynoSimulationResult simulationResult = runs.get(iRun).getResult();

				DynoSimulationEntry simulationEntry = simulationResult.getResultAt(rpm);

				if (simulationEntry != null) {
					String power = valuesFormat.format(simulationEntry.getPower());
					String torque = valuesFormat.format(simulationEntry.getTorque());
					dataEntry.put(POWER_FIELD + iRun, power);
					dataEntry.put(TORQUE_FIELD + iRun, torque);
				}
			}
			dataProvider.add(dataEntry);
		}
		root.put("dataProvider", dataProvider);
	}

	private void createGraphDefinitions(List<RunPlot> runs, String field) {

		final List<Map<String, Object>> graphs = new ArrayList<>();

		for (int iRun = 0; iRun < runs.size(); iRun++) {

			final RunPlot run = runs.get(iRun);

			final Map<String, Object> graph = new HashMap<>();
			graph.put("id", "Graph-" + field + iRun);
			graph.put("lineColor", run.getColor());
			graph.put("lineThickness", 1);
			graph.put("type", "smoothedLine");
			graph.put("xField", RPM_AXIS);
			graph.put("yField", field + iRun);
			graphs.add(graph);
		}
		root.put("graphs", graphs);
	}

	private void createAuxDataDefinition(List<RunPlot> runs, String field) {

		List<Map<String, Object>> dataProvider = new ArrayList<>();

		List<Double> rpmValues = getRpmValuesUnion(runs);
		for (Double rpm : rpmValues) {

			Map<String, Object> dataEntry = new HashMap<>();
			dataEntry.put(RPM_AXIS, rpm.intValue());

			for (int iRun = 0; iRun < runs.size(); iRun++) {
				LogValue<?> logValue = runs.get(iRun).getResult().getLogEntryAt(rpm, field);
				if (logValue != null) {
					dataEntry.put(field + iRun, valuesFormat.format(logValue.getValue()));
				}
			}
			dataProvider.add(dataEntry);
		}
		root.put("dataProvider", dataProvider);
	}

	private List<Double> getRpmValuesUnion(List<RunPlot> runs) {
		List<Double> rpmValues = new ArrayList<>();
		for (RunPlot run : runs) {
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

	private void createAxesDefinitions(String yTitle, List<RunPlot> runs) {

		Double maxYVal = null;

		if (runs != null) {
			maxYVal = 0.0;
			for (RunPlot run : runs) {
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
