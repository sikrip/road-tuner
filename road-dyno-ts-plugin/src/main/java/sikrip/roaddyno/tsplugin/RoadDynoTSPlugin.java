package sikrip.roaddyno.tsplugin;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.efiAnalytics.plugin.ApplicationPlugin;
import com.efiAnalytics.plugin.ecu.ControllerAccess;

public class RoadDynoTSPlugin implements ApplicationPlugin {

	private static final Logger LOGGER = LoggerFactory.getLogger(RoadDynoTSPlugin.class);

	private ControllerAccess controllerAccess;

	private PluginPanel pluginPanel;

	@Override
	public String getIdName() {
		return "road-dyno-ts-plugin";
	}

	@Override
	public int getPluginType() {
		//return TAB_PANEL; TODO does this works?
		return PERSISTENT_DIALOG_PANEL;
	}

	@Override
	public String getDisplayName() {
		return "Road Dyno Tuner Studio Plugin";
	}

	@Override
	public String getDescription() {
		return "A plugin that simulates a dynamometer by analyzing your wot runs on the fly!";
	}

	@Override
	public void initialize(ControllerAccess controllerAccess) {
		LOGGER.info("Plugin initializing. Configs found: ");
		for (String config : controllerAccess.getEcuConfigurationNames()) {
			LOGGER.info(config);
		}
		this.controllerAccess = controllerAccess;

		pluginPanel = PluginPanel.create();
	}

	@Override
	public boolean displayPlugin(String serialSignature) {
		LOGGER.info("SerialSignature: " + serialSignature);
		//TODO check this
		return serialSignature != null && !serialSignature.isEmpty();
	}

	@Override
	public boolean isMenuEnabled() {
		return false;
	}

	@Override
	public String getAuthor() {
		return "https://twitter.com/sikrip";
	}

	@Override
	public JComponent getPluginPanel() {
		return pluginPanel;
	}

	@Override
	public void close() {
		LOGGER.info("Plugin shutting down");
	}

	@Override
	public String getHelpUrl() {
		return "https://twitter.com/sikrip";
	}

	@Override
	public String getVersion() {
		return "0.1beta";
	}

	@Override
	public double getRequiredPluginSpec() {
		return 1.0;
	}
}
