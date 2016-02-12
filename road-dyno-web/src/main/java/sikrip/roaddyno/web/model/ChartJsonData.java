package sikrip.roaddyno.web.model;

import sikrip.roaddyno.model.DynoSimulationEntry;
import sikrip.roaddyno.model.DynoSimulationResult;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartJsonData {

    public static final String TORQUE_COLUMN = "column-2";
    public static final String POWER_COLUMN = "column-1";
    public static final String RPM_AXIS = "rpm";

    private Map<String, Object> root = new HashMap<>();
    private DecimalFormat df = df = new DecimalFormat("#.0");

    public ChartJsonData() {
        df.setRoundingMode(RoundingMode.DOWN);
    }

    public Map<String, Object> createJsonData(DynoSimulationResult dynoResult) {
        root.clear();

        root.put("type", "serial");
        root.put("categoryField", RPM_AXIS);
        root.put("startDuration", 1);
        root.put("startEffect", "easeOutSine");

        Map<String, Object> categoryAxis = new HashMap<>();
        categoryAxis.put("gridPosition", "start");
        categoryAxis.put("labelFrequency", 3);
        root.put("categoryAxis", categoryAxis);
        root.put("trendLines", new ArrayList<>());

        createGraphDefinitions(dynoResult.getName(), dynoResult.maxPower(), dynoResult.maxTorque());

        root.put("guides", new ArrayList<>());

        createValueAxisDefinition();

        root.put("allLabels", new ArrayList<>());
        root.put("balloon", new HashMap<>());

        createLegendDefinition();

        createTitlesDefinition();

        createDataDefinition(dynoResult);

        return root;
    }

    private void createDataDefinition(DynoSimulationResult dynoResult) {
        List<Map<String, Object>> dataProvider = new ArrayList<>();


        double[][] powerDataset = dynoResult.powerDataset();
        double[][] torqueDataset = dynoResult.torqueDataset();

        for (int i = 0; i < powerDataset[0].length; i++) {

            Map<String, Object> dataEntry = new HashMap<>();
            int rpm = (int) powerDataset[0][i];
            String power = df.format(powerDataset[1][i]);
            String torque = df.format(torqueDataset[1][i]);

            dataEntry.put(RPM_AXIS, rpm);
            dataEntry.put(POWER_COLUMN, power);
            dataEntry.put(TORQUE_COLUMN, torque);
            dataProvider.add(dataEntry);
        }

        root.put("dataProvider", dataProvider);
    }

    private void createTitlesDefinition() {
        List<Map<String, Object>> titles = new ArrayList<>();
        Map<String, Object> title = new HashMap<>();
        title.put("id", "Title-1");
        title.put("size", 15);
        title.put("text", "Dyno Plot");
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
        Map<String, String> valueAxis = new HashMap<>();
        valueAxis.put("id", "ValueAxis-1");
        valueAxis.put("title", "Hp | lb/ft");
        List<Map<String, String>> valueAxes = new ArrayList<>();
        valueAxes.add(valueAxis);
        root.put("valueAxes", valueAxes);
    }

    private void createGraphDefinitions(String name, DynoSimulationEntry maxPower, DynoSimulationEntry maxTorque) {

        List<Map<String, Object>> graphs = new ArrayList<>();
        Map<String, Object> graph = new HashMap<>();
        graph.put("balloonText", "[[title]] [[value]] @[[category]]");
        //graph.put("bullet", "round");
        //graph.put("bulletSize", 3);
        graph.put("id", "AmGraph-1");
        graph.put("lineColor", "#FF0000");
        graph.put("lineThickness", 3);
        graph.put("title", name + " power " + df.format(maxPower.getPower()) + " hp @" + (int)maxPower.getRpm());
        graph.put("type", "smoothedLine");
        graph.put("valueField", POWER_COLUMN);
        graphs.add(graph);

        graph = new HashMap<>();
        graph.put("balloonText", "[[title]] [[value]] @[[category]]");
        //graph.put("bullet", "square");
        graph.put("id", "AmGraph-2");
        graph.put("dashLength", 4);
        graph.put("lineColor", "#FF0000");
        graph.put("lineThickness", 2);
        graph.put("title", name + " torque " + df.format(maxTorque.getTorque()) + " lb/ft @" + (int)maxTorque.getRpm());
        graph.put("type", "smoothedLine");
        graph.put("valueField", TORQUE_COLUMN);
        graphs.add(graph);
        root.put("graphs", graphs);
    }

}
