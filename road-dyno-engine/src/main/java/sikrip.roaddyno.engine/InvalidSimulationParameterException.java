package sikrip.roaddyno.engine;

/**
 * Thrown when not enough log entries are provided to the simulator.
 */
public class InvalidSimulationParameterException extends Exception {

	public InvalidSimulationParameterException(String message) {
		super(message);
	}
}
