package sikrip.roaddyno.web.chart;

import java.util.UUID;

import sikrip.roaddyno.model.RunInfo;
import sikrip.roaddyno.model.DynoSimulationResult;
import sikrip.roaddyno.web.controller.SessionVehicleData;

/**
 * Wraps the info of a logged run, including the simulation result, name identifier and color.
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

	private Double finalGearRatio;
	private Double gearRatio;
	private Double tyreDiameter;
	private Double carWeight;
	private Double occupantsWeight;
	private Double frontalArea;
	private Double coefficientOfDrag;

	public UploadedRun() {
		id = UUID.randomUUID().toString();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFinalGearRatio(Double finalGearRatio) {
		this.finalGearRatio = finalGearRatio;
	}

	public void setGearRatio(Double gearRatio) {
		this.gearRatio = gearRatio;
	}

	public void setTyreDiameter(Double tyreDiameter) {
		this.tyreDiameter = tyreDiameter;
	}

	public void setCarWeight(Double carWeight) {
		this.carWeight = carWeight;
	}

	public void setOccupantsWeight(Double occupantsWeight) {
		this.occupantsWeight = occupantsWeight;
	}

	public void setFrontalArea(Double frontalArea) {
		this.frontalArea = frontalArea;
	}

	public void setCoefficientOfDrag(Double coefficientOfDrag) {
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
	public Double getFinalGearRatio() {
		return finalGearRatio;
	}

	@Override
	public Double getGearRatio() {
		return gearRatio;
	}

	@Override
	public Double getTyreDiameter() {
		return tyreDiameter;
	}

	@Override
	public Double getCarWeight() {
		return carWeight;
	}

	@Override
	public Double getOccupantsWeight() {
		return occupantsWeight;
	}

	@Override
	public Double getFrontalArea() {
		return frontalArea;
	}

	@Override
	public Double getCoefficientOfDrag() {
		return coefficientOfDrag;
	}

	public UploadedRun fromVehicleData(SessionVehicleData vehicleData){
		setFinalGearRatio(vehicleData.getFinalGearRatio());
		setGearRatio(vehicleData.getGearRatio());
		setTyreDiameter(vehicleData.getTyreDiameter());
		setCarWeight(vehicleData.getCarWeight());
		setOccupantsWeight(vehicleData.getOccupantsWeight());
		setFrontalArea(vehicleData.getFrontalArea());
		setCoefficientOfDrag(vehicleData.getCoefficientOfDrag());

		return this;
	}
}
