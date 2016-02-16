package sikrip.roaddyno.web.chart;

import java.util.UUID;

import sikrip.roaddyno.model.RunInfo;
import sikrip.roaddyno.model.DynoSimulationResult;

/**
 * Wraps info of a run, including the simulation result, name and identifier and color.
 */
public class UploadedRun implements RunInfo {

	/**
	 * The id of the run.
	 */
	private String id;

	/**
	 * The name of the run.
	 */
	private String name;

	/**
	 * The color of the run.
	 */
	private String color;

	/**
	 * The simulation result of the run.
	 */
	private DynoSimulationResult result;

	private double finalGearRatio = 4.312;
	private double gearRatio = 1.310;
	private double tyreDiameter = 528;
	private double carWeight = 905;
	private double occupantsWeight = 85;
	private double frontalArea = 1.7;
	private double coefficientOfDrag = 0.34;

	public UploadedRun() {
		id = UUID.randomUUID().toString();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFinalGearRatio(double finalGearRatio) {
		this.finalGearRatio = finalGearRatio;
	}

	public void setGearRatio(double gearRatio) {
		this.gearRatio = gearRatio;
	}

	public void setTyreDiameter(double tyreDiameter) {
		this.tyreDiameter = tyreDiameter;
	}

	public void setCarWeight(double carWeight) {
		this.carWeight = carWeight;
	}

	public void setOccupantsWeight(double occupantsWeight) {
		this.occupantsWeight = occupantsWeight;
	}

	public void setFrontalArea(double frontalArea) {
		this.frontalArea = frontalArea;
	}

	public void setCoefficientOfDrag(double coefficientOfDrag) {
		this.coefficientOfDrag = coefficientOfDrag;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DynoSimulationResult getResult() {
		return result;
	}

	public void setResult(DynoSimulationResult result) {
		this.result = result;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getFinalGearRatio() {
		return finalGearRatio;
	}

	@Override
	public double getGearRatio() {
		return gearRatio;
	}

	@Override
	public double getTyreDiameter() {
		return tyreDiameter;
	}

	@Override
	public double getCarWeight() {
		return carWeight;
	}

	@Override
	public double getOccupantsWeight() {
		return occupantsWeight;
	}

	@Override
	public double getFrontalArea() {
		return frontalArea;
	}

	@Override
	public double getCoefficientOfDrag() {
		return coefficientOfDrag;
	}
}
