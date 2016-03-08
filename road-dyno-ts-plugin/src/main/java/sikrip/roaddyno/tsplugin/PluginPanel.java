package sikrip.roaddyno.tsplugin;

import java.awt.*;

import javax.swing.*;

public class PluginPanel extends JPanel {

	static PluginPanel create() {
		PluginPanel pluginPanel = new PluginPanel();
		pluginPanel.setPreferredSize(new Dimension(400, 400));
		pluginPanel.setLayout(new BorderLayout());

		pluginPanel.add(new JLabel("Road dyno TS plugin!"), BorderLayout.NORTH);
		return pluginPanel;
	}
}
