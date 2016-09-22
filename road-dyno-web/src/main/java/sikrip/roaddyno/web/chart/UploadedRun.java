package sikrip.roaddyno.web.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import sikrip.roaddyno.engine.AccelerationBounds;
import sikrip.roaddyno.engine.DynoSimulationEntry;
import sikrip.roaddyno.engine.DynoSimulationResult;
import sikrip.roaddyno.engine.RunInfo;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.LogFileData;
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

	private boolean active = true;

	/**
	 * The simulation result of the run.
	 */
	private DynoSimulationResult result;

	/**
	 * The raw log entries that produced this result.
	 */
	private final List<LogEntry> logEntries = new ArrayList<>();

	private boolean rpmBased;

	private Double finalGearRatio;
	private Double gearRatio;
	private Double tyreDiameter;
	private Double carWeight;
	private Double occupantsWeight;
	private Double frontalArea;
	private Double coefficientOfDrag;

	private final List<AccelerationBounds> accelerations = new ArrayList<>();

	private int selectedAccelerationIdx;

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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public DynoSimulationEntry getMaxPower() {
		return result != null ? result.maxPower() : null;
	}

	public DynoSimulationEntry getMaxTorque() {
		return result != null ? result.maxTorque() : null;
	}

	public List<AccelerationBounds> getAccelerations(){
		return accelerations;
	}

	public void addAccelerations(List<AccelerationBounds> accelerations) {
		this.accelerations.addAll(accelerations);
	}

	public UploadedRun fromVehicleData(SessionVehicleData vehicleData) {
		setFinalGearRatio(vehicleData.getFinalGearRatio());
		setGearRatio(vehicleData.getGearRatio());
		setTyreDiameter(vehicleData.getTyreDiameter());
		setCarWeight(vehicleData.getCarWeight());
		setOccupantsWeight(vehicleData.getOccupantsWeight());
		setFrontalArea(vehicleData.getFrontalArea());
		setCoefficientOfDrag(vehicleData.getCoefficientOfDrag());
		return this;
	}

	public List<LogEntry> getSelectedLogEntries() {
		AccelerationBounds selectedAcceleration = accelerations.get(selectedAccelerationIdx);
		return logEntries.subList(selectedAcceleration.getStart(), selectedAcceleration.getEnd());
	}

	public void addLogEntries(LogFileData logFileData) {
		this.logEntries.addAll(logFileData.getLogEntries());
		this.rpmBased = logFileData.isRpmBased();
	}

	public boolean isRpmBased() {
		return rpmBased;
	}

	public int getSelectedAccelerationIdx() {
		return selectedAccelerationIdx;
	}

	public void setSelectedAccelerationIdx(int selectedAccelerationIdx) {
		this.selectedAccelerationIdx = selectedAccelerationIdx;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		UploadedRun run = (UploadedRun) o;

		return id.equals(run.id);

	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
