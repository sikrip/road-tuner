package sikrip.roaddyno.standalone;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import sikrip.roaddyno.model.DynoSimulationResult;
import sikrip.roaddyno.model.LogEntry;

final class AuxiliaryChart {

    private static final NumberAxis rpmAxis = new NumberAxis("RPM");
    private static final NumberAxis yAxisLeft = new NumberAxis();

    private AuxiliaryChart() {
    }

    static JFreeChart emptyChart() {
        return new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, createPlot(), false);
    }

    static JFreeChart chartForRuns(List<DynoRunGuiEntry> runs, String valueKey) {

        final XYPlot plot = createPlot();

        for (DynoRunGuiEntry runWrapper : runs) {

            if (runWrapper.isActive()) {
                DynoSimulationResult run = runWrapper.getDynoSimulationResult();

                int dataSetIdx = plot.getDatasetCount(); // one based

                XYSplineRenderer xySplineRenderer = new XYSplineRenderer();
                NumberFormat numberFormat = DecimalFormat.getInstance();
                numberFormat.setGroupingUsed(false);
                numberFormat.setMaximumFractionDigits(2);
                numberFormat.setRoundingMode(RoundingMode.UP);
                StandardXYToolTipGenerator toolTipGenerator = new StandardXYToolTipGenerator("{0} {2} @{1}", numberFormat, numberFormat);

                xySplineRenderer.setBaseToolTipGenerator(toolTipGenerator);

                xySplineRenderer.setSeriesStroke(0, new BasicStroke(2f));
                xySplineRenderer.setSeriesPaint(0, runWrapper.getColor());

                xySplineRenderer.setSeriesShapesVisible(0, false);

                DefaultXYDataset dataset = new DefaultXYDataset();
                dataset.addSeries(run.getName() + " " + valueKey, getDataSet(valueKey, run, runWrapper.getLogEntries()));

                plot.setDataset(dataSetIdx, dataset);
                plot.setRenderer(dataSetIdx, xySplineRenderer);

                yAxisLeft.setLabel(valueKey);
            }
        }
        return new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    }

    private static XYPlot createPlot() {

        final XYPlot plot = new XYPlot(new DefaultXYDataset(), rpmAxis, yAxisLeft, new XYLineAndShapeRenderer(true, false));
        plot.setOrientation(PlotOrientation.VERTICAL);

        rpmAxis.setPlot(plot);
        rpmAxis.setAutoRangeIncludesZero(false);

        plot.setDomainAxis(rpmAxis);

        yAxisLeft.setPlot(plot);
        yAxisLeft.setAutoRangeIncludesZero(false);
        plot.setRangeAxis(0, yAxisLeft);

        return plot;
    }

    private static double[][] getDataSet(String key, DynoSimulationResult run, List<LogEntry> entries) {
        double[][] dataset = new double[2][run.getEntriesSize()];

        for (int i = 0; i < run.getEntriesSize(); i++) {
            dataset[0][i] = run.getSmoothedRpmAt(i);
            dataset[1][i] = entries.get(i).get(key).getValue();
        }

        return dataset;
    }
}
