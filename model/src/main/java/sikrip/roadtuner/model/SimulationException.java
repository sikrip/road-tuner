package sikrip.roaddyno.model;

/**
 * Thrown when the simulator cannot be completed.
 */
public class SimulationException extends Exception {

	public SimulationException(String message) {
		super(message);
	}

	public SimulationException(String message, Throwable cause) {
		super(message, cause);
	}
}
