package sikrip.roaddyno.standalone;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import sikrip.roaddyno.model.DynoSimulationResult;
import sikrip.roaddyno.model.DynoSimulationEntry;

final class DynoChart {

    private static final double Y_AXIS_MARGIN = 20d;

    private static final NumberAxis rpmAxis = new NumberAxis("RPM");
    private static final NumberAxis hpAxis = new NumberAxis("hp");
    private static final NumberAxis torqueAxis = new NumberAxis("lb.ft");

    private DynoChart() {
    }

    static JFreeChart emptyChart() {
        return new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, createPlot(), false);
    }

    static JFreeChart chartForRuns(List<DynoRunGuiEntry> runs) {

        final XYPlot plot = createPlot();

        for (DynoRunGuiEntry runWrapper : runs) {
            if (runWrapper.isActive()) {
                DynoSimulationResult run = runWrapper.getDynoSimulationResult();

                int dataSetIdx = plot.getDatasetCount();

               DynoSimulationEntry maxPower = run.maxPower();
               DynoSimulationEntry maxTorque = run.maxTorque();

               DynoSimulationEntry minPower = run.minPower();
               DynoSimulationEntry minTorque = run.minTorque();

                double yMin = Math.min(Math.min(minPower.getPower(), minTorque.getTorque()) - Y_AXIS_MARGIN, hpAxis.getLowerBound());
                double yMax = Math.max(Math.max(maxPower.getPower(), maxTorque.getTorque()) + Y_AXIS_MARGIN, hpAxis.getUpperBound());

                hpAxis.setRange(yMin, yMax);
                torqueAxis.setRange(yMin, yMax);

                XYSplineRenderer xySplineRenderer = new XYSplineRenderer();
                NumberFormat numberFormat = DecimalFormat.getInstance();
                numberFormat.setGroupingUsed(false);
                numberFormat.setMaximumFractionDigits(0);
                numberFormat.setRoundingMode(RoundingMode.UP);
                StandardXYToolTipGenerator toolTipGenerator = new StandardXYToolTipGenerator("{0} {2} @{1}", numberFormat, numberFormat);

                xySplineRenderer.setBaseToolTipGenerator(toolTipGenerator);

                xySplineRenderer.setSeriesStroke(0, new BasicStroke(3f));
                xySplineRenderer.setSeriesStroke(1, new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f}, 0.0f));

                xySplineRenderer.setSeriesPaint(0, runWrapper.getColor());
                xySplineRenderer.setSeriesPaint(1, runWrapper.getColor());

                xySplineRenderer.setSeriesShapesVisible(0, false);
                xySplineRenderer.setSeriesShapesVisible(1, false);

                // Add series (power/torque)
                DefaultXYDataset dataset = new DefaultXYDataset();
                dataset.addSeries(run.getName() + " power", run.powerDataset());
                dataset.addSeries(run.getName() + " torque", run.torqueDataset());

                plot.setDataset(dataSetIdx, dataset);
                plot.setRenderer(dataSetIdx, xySplineRenderer);

                String annotationText = getPlotAnnotationText(run.getName(), maxPower, maxTorque);

                double xPosition = (rpmAxis.getLowerBound() + rpmAxis.getUpperBound()) / 2.0;
                XYTextAnnotation annotation = new XYTextAnnotation(annotationText, xPosition, hpAxis.getLowerBound() + 8 * dataSetIdx);

                annotation.setFont(new Font(JFreeChart.DEFAULT_TITLE_FONT.getName(), Font.PLAIN, 14));
                annotation.setPaint(runWrapper.getColor());
                plot.addAnnotation(annotation);
            }
        }
        return new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    }

    private static XYPlot createPlot() {
        final XYPlot plot = new XYPlot(new DefaultXYDataset(), rpmAxis, hpAxis, new XYLineAndShapeRenderer(true, false));
        plot.setOrientation(PlotOrientation.VERTICAL);

        rpmAxis.setPlot(plot);
        rpmAxis.setAutoRangeIncludesZero(false);
        plot.setDomainAxis(rpmAxis);

        hpAxis.setPlot(plot);
        hpAxis.setRange(40, 80);
        plot.setRangeAxis(0, hpAxis);

        torqueAxis.setPlot(plot);
        torqueAxis.setRange(40, 80);
        plot.setRangeAxis(1, torqueAxis);

        return plot;
    }

    private static String getPlotAnnotationText(String title, DynoSimulationEntry maxPower, DynoSimulationEntry maxTorque) {
        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.UP);

        return title + ": " + df.format(maxPower.getPower()) + " HP @" + df.format(maxPower.getRpm()) +
                " | " + df.format(maxTorque.getTorque()) + " lb.ft @" + df.format(maxTorque.getRpm());
    }
}
