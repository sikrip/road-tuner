package sikrip.roaddyno.tsplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.efiAnalytics.plugin.ecu.ControllerAccess;
import com.efiAnalytics.plugin.ecu.ControllerException;
import com.efiAnalytics.plugin.ecu.OutputChannel;
import com.efiAnalytics.plugin.ecu.OutputChannelClient;

import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.LogValue;

import static sikrip.roaddyno.tsplugin.RoadDynoTSPlugin.*;

public final class EcuLogger implements OutputChannelClient {

	private static final String TIME_CHANNEL = "Time";
	private static final String RPM_CHANNEL = "RPM";
	private static final String TPS_CHANNEL = "TPS";

	private final ControllerAccess controllerAccess;
	private final Map<String, List<Double>> loggedValues = new HashMap<>();

	private EcuLogger(ControllerAccess controllerAccess) {
		this.controllerAccess = controllerAccess;
	}

	public static EcuLogger create(ControllerAccess controllerAccess) {
		return new EcuLogger(controllerAccess);
	}

	public void clear() {
		loggedValues.clear();
	}

	public void registerChannel(String configurationName, String channelName) {
		try {
			loggedValues.put(channelName, new ArrayList<Double>());
			OutputChannel outputChannel = controllerAccess.getOutputChannelServer().getOutputChannel(configurationName, channelName);
			controllerAccess.getOutputChannelServer().subscribe(configurationName, outputChannel.getName(), this);
		} catch (ControllerException e) {
			//TODO handle
			e.printStackTrace();
		}
	}

	@Override
	public void setCurrentOutputChannelValue(String channel, double value) {
		loggedValues.get(channel).add(value);
	}

	public List<LogEntry> getLogEntries() {

		List<LogEntry> logEntries = new ArrayList<>();

		for (int i = 0; i < loggedValues.get(TIME_CHANNEL).size(); i++) {
			Map<String, LogValue<Double>> values = new HashMap<>();
			values.put(TIME_CHANNEL, new LogValue<>(loggedValues.get(TIME_CHANNEL).get(i), TIME_CHANNEL));
			values.put(RPM_CHANNEL, new LogValue<>(loggedValues.get(RPM_CHANNEL).get(i), RPM_CHANNEL));
			values.put(TPS_CHANNEL, new LogValue<>(loggedValues.get(TPS_CHANNEL).get(i), TPS_CHANNEL));
			logEntries.add(new LogEntry(values, TIME_CHANNEL, RPM_CHANNEL, TPS_CHANNEL));
		}

		return logEntries;
	}

}
