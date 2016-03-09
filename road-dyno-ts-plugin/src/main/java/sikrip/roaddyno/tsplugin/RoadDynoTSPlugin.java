package sikrip.roaddyno.tsplugin;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.efiAnalytics.plugin.ApplicationPlugin;
import com.efiAnalytics.plugin.ecu.ControllerAccess;
import com.efiAnalytics.plugin.ecu.ControllerException;
import com.efiAnalytics.plugin.ecu.OutputChannelClient;

public class RoadDynoTSPlugin implements ApplicationPlugin, OutputChannelClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(RoadDynoTSPlugin.class);

	private final double TPS_WOT_THRESHOLD = 97;

	private ControllerAccess controllerAccess;

	private String ecuConfigName;

	private PluginPanel pluginPanel;

	private EcuLogger ecuLogger;

	private boolean wotLogging = false;

	private String timeChannelName;
	private String rpmChannelName;
	private String tpsChannelName;

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
		LOGGER.info("Plugin initializing...");

		this.controllerAccess = controllerAccess;
		pluginPanel = PluginPanel.create();
		ecuLogger = EcuLogger.create(controllerAccess);

		String[] ecuConfigurationNames = controllerAccess.getEcuConfigurationNames();
		if (ecuConfigurationNames != null && ecuConfigurationNames.length > 0) {
			LOGGER.info("Configuring plugin with ECU configuration: " + ecuConfigurationNames[0]);
			ecuConfigName = ecuConfigurationNames[0];

			if (resolveChannelNames()) {
				try {
					controllerAccess.getOutputChannelServer().subscribe(ecuConfigName, tpsChannelName, this);
					LOGGER.info("Registered WOT listener.");
				} catch (ControllerException e) {
					LOGGER.error("Cannot register WOT listener.", e);
				}
			} else {
				//TODO manual resolution needed here
				LOGGER.error("Could not auto-resolve channels for logging.");
			}
		} else {
			LOGGER.error("No ECU configurations found.");
		}
	}

	private boolean resolveChannelNames() {
		timeChannelName = null;
		rpmChannelName = null;
		tpsChannelName = null;
		try {
			for (String channel : controllerAccess.getOutputChannelServer().getOutputChannels(ecuConfigName)) {
				if (timeChannelName == null && "secL".equalsIgnoreCase(channel)) {
					timeChannelName = channel;
				} else if (rpmChannelName == null && "rpm".equalsIgnoreCase(channel)) {
					rpmChannelName = channel;
				} else if (tpsChannelName == null && "tps".equalsIgnoreCase(channel)) {
					tpsChannelName = channel;
				}
			}
			LOGGER.info("Resolved the following channels: time=" + timeChannelName + " rpm=" + rpmChannelName + " tps=" + tpsChannelName);
		} catch (ControllerException e) {
			LOGGER.error("Cannot resolve time, tps and rpm channels");
			return false;
		}

		return timeChannelName != null && rpmChannelName != null && tpsChannelName != null;
	}

	@Override
	public boolean displayPlugin(String serialSignature) {
		LOGGER.info("SerialSignature: " + serialSignature);
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
		controllerAccess.getOutputChannelServer().unsubscribe(this);
		controllerAccess.getOutputChannelServer().unsubscribe(ecuLogger);
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

	@Override
	public void setCurrentOutputChannelValue(String ignored, double tps) {
		if (tps >= TPS_WOT_THRESHOLD && !wotLogging) {
			try {
				ecuLogger.startLogging(ecuConfigName, timeChannelName, rpmChannelName, tpsChannelName);
				wotLogging = true;
				LOGGER.info("WOT logging started!");
			} catch (ControllerException e) {
				LOGGER.error("Cannot log WOT.", e);
			}
		} else if (wotLogging) {
			ecuLogger.stopLogging();
			LOGGER.info("WOT logging finished!");
		}
	}

	public static void main(String[] args) {
		//TODO check if this is needed
	}
}
