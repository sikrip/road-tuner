package sikrip.roaddyno.model;

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
	double getFinalGearRatio();

	/**
	 * The ratio of the gear used in the run.
	 *
	 * @return ratio of the gear used in the run
	 */
	double getGearRatio();

	/**
	 * The diameter of the driving tyres of the car.
	 *
	 * @return diameter of the driving tyres of the car
	 */
	double getTyreDiameter();

	/**
	 * The curb weight of the car.
	 *
	 * @return curb weight of the car
	 */
	double getCarWeight();

	/**
	 * The weight of the occupants during the run.
	 *
	 * @return weight of the occupants during the run
	 */
	double getOccupantsWeight();

	/**
	 * The frontal area of the car.
	 *
	 * @return frontal area of the car
	 */
	double getFrontalArea();

	/**
	 * The coefficient of drag of the car.
	 *
	 * @return coefficient of drag of the car
	 */
	double getCoefficientOfDrag();
}
