package sikrip.roaddyno.standalone;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;

import sikrip.roaddyno.model.RunInfo;
import sikrip.roaddyno.model.DynoSimulationResult;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.standalone.util.FontUtil;

final class DynoRunGuiEntry implements ActionListener {

	private DynoSimulationResult dynoSimulationResult;
	private final Color color;

	private final JPanel panel = new JPanel();

	private final JButton closeButton = new JButton("x");
	private final JCheckBox activeCheck = new JCheckBox();
	private final JTextField runName = new JTextField();
	private final JButton updateButton = new JButton("update");

	private final RunInfoPanel infoPanel = new RunInfoPanel();

	DynoRunGuiEntry(DynoSimulationResult dynoSimulationResult, Color color, String name, double fgr, double gr, double tyreDiameter,
			double carWeight, double occupantsWeight, double fa, double cd) {

		this.dynoSimulationResult = dynoSimulationResult;
		this.color = color;

		panel.setLayout(new BorderLayout());

		panel.setBorder(BorderFactory.createEtchedBorder());

		JPanel north = new JPanel();
		north.setLayout(new BoxLayout(north, BoxLayout.LINE_AXIS));
		north.add(closeButton);
		north.add(activeCheck);
		north.add(runName);
		north.add(Box.createHorizontalGlue());
		north.add(updateButton);

		panel.add(north, BorderLayout.NORTH);
		runName.setText(name);
		runName.setEditable(false);
		runName.setForeground(color);

		infoPanel.setInfo(name, fgr, gr, tyreDiameter, carWeight, occupantsWeight, fa, cd);
		panel.add(infoPanel, BorderLayout.CENTER);

		closeButton.addActionListener(this);
		activeCheck.addActionListener(this);
		updateButton.addActionListener(this);

		FontUtil.changeFont(panel, new Font("Dialog", Font.PLAIN, 11));

		activeCheck.setSelected(true);

		runName.setFont(new Font("Dialog", Font.BOLD, 12));
		closeButton.setFont(new Font("Dialog", Font.BOLD, 12));
	}

	JPanel getPanel() {
		return panel;
	}

	DynoSimulationResult getDynoSimulationResult() {
		return dynoSimulationResult;
	}

	void setDynoSimulationResult(DynoSimulationResult dynoSimulationResult) {
		this.dynoSimulationResult = dynoSimulationResult;
	}

	List<LogEntry> getLogEntries() {
		return null;
	}

	Color getColor() {
		return color;
	}

	boolean isActive() {
		return activeCheck.isSelected();
	}

	RunInfo getInfo() {
		return infoPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == closeButton) {
			RoadDynoGui.getInstance().removeRun(this);
		} else if (e.getSource() == activeCheck) {
			RoadDynoGui.getInstance().plotRuns();
		} else if (e.getSource() == updateButton) {
			RoadDynoGui.getInstance().updateRun(this);
		}
	}

}
