package sikrip.roaddyno.standalone;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.jfree.chart.ChartPanel;

import sikrip.roaddyno.engine.DynoSimulator;
import sikrip.roaddyno.logreader.CarLogReader;
import sikrip.roaddyno.logreader.MegasquirtLogReader;
import sikrip.roaddyno.model.DynoSimulationResult;
import sikrip.roaddyno.model.LogEntry;

final class RoadDynoGui extends JFrame implements ActionListener {

	static final int TPS_START_THRESHOLD = 96;
	static final int MAX_PLOTS = 12;

	private final JPanel mainPanel = new JPanel(new BorderLayout());
	private final JPanel runsPanel = new JPanel();
	private final JScrollPane runsScrollPane = new JScrollPane(runsPanel);
	private final ChartPanel chartPanel = new ChartPanel(null);
	private final ChartPanel auxiliaryChartPanel = new ChartPanel(null);

	private final JButton addRunButton = new JButton("Add run");

	private DynoRunAddDialog dynoRunAddDialog = new DynoRunAddDialog();

	private final List<DynoRunGuiEntry> dynoRuns = new ArrayList<>();

	private static RoadDynoGui roadDynoGui;

	RoadDynoGui() {
		roadDynoGui = this;
	}

	static RoadDynoGui getInstance() {
		return roadDynoGui;
	}

	void createGui() throws HeadlessException, IOException {

		setTitle("Road Dyno");

		mainPanel.setPreferredSize(new Dimension(1000, 700));

		JPanel center = new JPanel(new BorderLayout());
		center.add(chartPanel, BorderLayout.CENTER);

		auxiliaryChartPanel.setPreferredSize(new Dimension(500, 200));

		center.add(auxiliaryChartPanel, BorderLayout.SOUTH);

		runsPanel.setLayout(new BoxLayout(runsPanel, BoxLayout.PAGE_AXIS));

		JPanel left = new JPanel(new BorderLayout());
		left.setPreferredSize(new Dimension(320, 500));
		left.add(addRunButton, BorderLayout.NORTH);

		left.add(runsScrollPane, BorderLayout.CENTER);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				left, center);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(320);

		mainPanel.add(splitPane, BorderLayout.CENTER);

		setContentPane(mainPanel);

		addRunButton.addActionListener(this);
		chartPanel.setChart(DynoChart.emptyChart());
		auxiliaryChartPanel.setChart(AuxiliaryChart.emptyChart());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addRunButton) {
			addRun();
		}
	}

	private void addRun() {
		if (dynoRunAddDialog.showDynoDialog()) {
			try {
				CarLogReader logReader = new MegasquirtLogReader();
				List<LogEntry> logEntries = logReader.readLog(dynoRunAddDialog.getRunFilePath(), TPS_START_THRESHOLD);

				DynoSimulationResult run = DynoSimulator.run(
						logEntries,
						dynoRunAddDialog.getRunName(),
						dynoRunAddDialog.getFGR(),
						dynoRunAddDialog.getGearRatio(),
						dynoRunAddDialog.getTyreDiameter(),
						dynoRunAddDialog.getCarWeight(),
						dynoRunAddDialog.getOccupantsWeight(),
						dynoRunAddDialog.getFrontalArea(),
						dynoRunAddDialog.getCD());

				DynoRunGuiEntry runGuiEntry = new DynoRunGuiEntry(run, ColorProvider.pop());
				runGuiEntry.getPanel().setMaximumSize(new Dimension(310, 200));

				runsPanel.add(runGuiEntry.getPanel());
				runsPanel.revalidate();
				runsPanel.repaint(100L);

				dynoRuns.add(runGuiEntry);

				plotRuns();

				if (dynoRuns.size() == MAX_PLOTS) {
					addRunButton.setEnabled(false);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Cannot add run", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	void removeRun(DynoRunGuiEntry runWrapper) {
		ColorProvider.push(runWrapper.getColor());
		dynoRuns.remove(runWrapper);
		runsPanel.remove(runWrapper.getPanel());
		runsPanel.revalidate();
		runsPanel.repaint(100L);
		plotRuns();
		if (!addRunButton.isEnabled()) {
			addRunButton.setEnabled(true);
		}
	}

	void plotRuns() {
		if (dynoRuns.isEmpty()) {
			ColorProvider.reset();
		}
		chartPanel.setChart(DynoChart.chartForRuns(dynoRuns));
		auxiliaryChartPanel.setChart(AuxiliaryChart.chartForRuns(dynoRuns, "AFR"));
		//auxiliaryChartPanel.setChart(AuxiliaryChart.chartForRuns(dynoRuns, "SPK: Spark Advance"));
	}

	void updateRun(DynoRunGuiEntry dynoRunGuiEntry) {
		try {
			DynoSimulationResult run = DynoSimulator.run(
					dynoRunGuiEntry.getLogEntries(),
					dynoRunGuiEntry.getInfo().getName(),
					dynoRunGuiEntry.getInfo().getFinalGearRatio(),
					dynoRunGuiEntry.getInfo().getGearRatio(),
					dynoRunGuiEntry.getInfo().getTyreDiameter(),
					dynoRunGuiEntry.getInfo().getCarWeight(),
					dynoRunGuiEntry.getInfo().getOccupantsWeight(),
					dynoRunGuiEntry.getInfo().getFrontalArea(),
					dynoRunGuiEntry.getInfo().getCoefficientOfDrag());

			dynoRunGuiEntry.setDynoSimulationResult(run);

			plotRuns();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Cannot update run", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) throws IOException {
		RoadDynoGui roadDynoGui = new RoadDynoGui();
		roadDynoGui.createGui();
		roadDynoGui.pack();
		roadDynoGui.setLocationRelativeTo(null);
		roadDynoGui.setVisible(true);
		roadDynoGui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
