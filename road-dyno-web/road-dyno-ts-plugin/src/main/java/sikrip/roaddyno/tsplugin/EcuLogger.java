package sikrip.roaddyno.tsplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.efiAnalytics.plugin.ecu.ControllerAccess;
import com.efiAnalytics.plugin.ecu.ControllerException;
import com.efiAnalytics.plugin.ecu.OutputChannelClient;

import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.LogValue;

public final class EcuLogger implements OutputChannelClient {

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

	public void startLogging(String configurationName, String timeChannelName, String rpmChannelName, String tpsChannelName) throws ControllerException {
		loggedValues.put(timeChannelName, new ArrayList<Double>());
		controllerAccess.getOutputChannelServer().subscribe(configurationName, timeChannelName, this);

		loggedValues.put(rpmChannelName, new ArrayList<Double>());
		controllerAccess.getOutputChannelServer().subscribe(configurationName, rpmChannelName, this);

		loggedValues.put(tpsChannelName, new ArrayList<Double>());
		controllerAccess.getOutputChannelServer().subscribe(configurationName, tpsChannelName, this);
	}

	public void stopLogging() {
		controllerAccess.getOutputChannelServer().unsubscribe(this);
	}

	@Override
	public void setCurrentOutputChannelValue(String channel, double value) {
		loggedValues.get(channel).add(value);
	}

	public List<LogEntry> getLogEntries() {

		List<LogEntry> logEntries = new ArrayList<>();

		/*for (int i = 0; i < loggedValues.get(TIME_CHANNEL).size(); i++) {
			Map<String, LogValue<Double>> values = new HashMap<>();
			values.put(TIME_CHANNEL, new LogValue<>(loggedValues.get(TIME_CHANNEL).get(i), TIME_CHANNEL));
			values.put(RPM_CHANNEL, new LogValue<>(loggedValues.get(RPM_CHANNEL).get(i), RPM_CHANNEL));
			values.put(TPS_CHANNEL, new LogValue<>(loggedValues.get(TPS_CHANNEL).get(i), TPS_CHANNEL));
			logEntries.add(new LogEntry(values, TIME_CHANNEL, RPM_CHANNEL, TPS_CHANNEL));
		}*/

		return logEntries;
	}

}
