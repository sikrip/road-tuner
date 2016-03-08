package sikrip.roaddyno.tsplugin;

import java.util.logging.Logger;

import javax.swing.*;

import com.efiAnalytics.plugin.ApplicationPlugin;
import com.efiAnalytics.plugin.ecu.ControllerAccess;
import com.efiAnalytics.plugin.ecu.OutputChannelClient;

public class RoadDynoTSPlugin implements ApplicationPlugin, OutputChannelClient {

	private static final Logger LOGGER = Logger.getLogger("RoadDynoTSPlugin");

	public static final int TPS_THRESHOLD = 96;
	public static final String TIME_CHANNEL = "Time";
	public static final String RPM_CHANNEL = "RPM";
	public static final String TPS_CHANNEL = "TPS";

	private ControllerAccess controllerAccess;

	private PluginPanel pluginPanel;

	private EcuLogger ecuLogger;

	public boolean logging = false;

	private String getMainConfigName() {
		return controllerAccess.getEcuConfigurationNames()[0];
	}

	private void startLogging() {
		ecuLogger.clear();
		ecuLogger.registerChannel(getMainConfigName(), TIME_CHANNEL);
		ecuLogger.registerChannel(getMainConfigName(), RPM_CHANNEL);
		ecuLogger.registerChannel(getMainConfigName(), TPS_CHANNEL);
		logging = true;
	}

	private void stopLogging() {
		controllerAccess.getOutputChannelServer().unsubscribe(ecuLogger);
		logging = false;
		pluginPanel.generatePlot(ecuLogger.getLogEntries());
	}

	@Override
	public String getIdName() {
		return "road-dyno-ts-plugin";
	}

	@Override
	public int getPluginType() {
		return TAB_PANEL; // TODO does this works?
		//return PERSISTENT_DIALOG_PANEL;
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
		ecuLogger = EcuLogger.create(controllerAccess);
	}

	@Override
	public boolean displayPlugin(String serialSignature) {
		LOGGER.info("SerialSignature: " + serialSignature);
		//TODO check this
		return serialSignature != null && !serialSignature.isEmpty();
	}

	@Override
	public boolean isMenuEnabled() {
		return true;
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

	public static void main(String[] args) {
		//TODO check if this is needed
	}

	@Override
	public void setCurrentOutputChannelValue(String ignored, double v) {
		if (v >= TPS_THRESHOLD) {
			startLogging();
		} else if (logging) {
			stopLogging();
		}
	}
}
