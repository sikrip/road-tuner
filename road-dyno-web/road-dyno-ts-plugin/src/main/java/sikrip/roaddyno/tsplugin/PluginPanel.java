package sikrip.roaddyno.tsplugin;


import java.awt.*;
import java.util.List;

import javax.swing.*;

import sikrip.roaddyno.model.LogEntry;

public class PluginPanel extends JPanel {

	static PluginPanel create() {
		PluginPanel pluginPanel = new PluginPanel();
		pluginPanel.setPreferredSize(new Dimension(800, 600));
		pluginPanel.setLayout(new BorderLayout());

		pluginPanel.add(new JLabel("Road dyno TS plugin"), BorderLayout.NORTH);
		return pluginPanel;
	}

	public void generatePlot(List<LogEntry> logEntries){

	}
}
