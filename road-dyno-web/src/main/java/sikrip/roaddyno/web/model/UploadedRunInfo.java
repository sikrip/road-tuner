package sikrip.roaddyno.web.model;

import sikrip.roaddyno.model.DynoRunInfo;

public class UploadedRunInfo implements DynoRunInfo {

	private String name;
	private double finalGearRatio = 4.312;
	private double gearRatio = 1.310;
	private double tyreDiameter = 528;
	private double carWeight = 905;
	private double occupantsWeight = 85;
	private double frontalArea = 1.7;
	private double coefficientOfDrag = 0.34;

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
