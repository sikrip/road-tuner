package sikrip.roaddyno.engine;

import java.util.List;

/**
 * API for classes representing a run.
 */
public interface RunInfo {

	/**
	 * The name of the run.
	 *
	 * @return name of the run
	 */
	String getName();

	/**
	 * The final gear ratio of the cars gearbox.
	 *
	 * @return final gear ratio of the cars gearbox
	 */
	Double getFinalGearRatio();

	/**
	 * The ratio of the gear used in the run.
	 *
	 * @return ratio of the gear used in the run
	 */
	Double getGearRatio();

	/**
	 * The diameter of the driving tyres of the car.
	 *
	 * @return diameter of the driving tyres of the car
	 */
	Double getTyreDiameter();

	/**
	 * The curb weight of the car.
	 *
	 * @return curb weight of the car
	 */
	Double getCarWeight();

	/**
	 * The weight of the occupants during the run.
	 *
	 * @return weight of the occupants during the run
	 */
	Double getOccupantsWeight();

	/**
	 * The frontal area of the car.
	 *
	 * @return frontal area of the car
	 */
	Double getFrontalArea();

	/**
	 * The coefficient of drag of the car.
	 *
	 * @return coefficient of drag of the car
	 */
	Double getCoefficientOfDrag();
}
