package sikrip.roaddyno.web.model;

import java.util.List;
import java.util.UUID;

import sikrip.roaddyno.engine.DynoSimulationEntry;
import sikrip.roaddyno.engine.DynoSimulationResult;
import sikrip.roaddyno.engine.RunInfo;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.web.controller.SessionVehicleData;

/**
 * Wraps the info of a logged run, including the simulation result, name identifier and color.
 */
public class LoggedRunsEntry implements RunInfo, Comparable<LoggedRunsEntry> {

	/**
	 * The index of this entry.
	 */
	private int index;

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
	 * Contains the read log file data along with possible WOT runs within these data.
	 */
	private LogFileData logFileData;

	/**
	 * The selected WOT run to be used for the dyno simulation.
	 */
	private int selectedAccelerationIdx;

	private Double finalGearRatio;
	private Double gearRatio;
	private Double tyreDiameter;
	private Double carWeight;
	private Double occupantsWeight;
	private Double frontalArea;
	private Double coefficientOfDrag;

	public LoggedRunsEntry() {
		/*used by spring web*/
	}

	public LoggedRunsEntry(int index) {
		id = UUID.randomUUID().toString();
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
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

	public List<WOTRun> getAccelerations() {
		return logFileData.getWOTRuns();
	}

	public LoggedRunsEntry updateVehicleData(SessionVehicleData vehicleData) {
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
		WOTRun selectedAcceleration = logFileData.getWOTRuns().get(selectedAccelerationIdx);
		return logFileData.getLogEntries().subList(selectedAcceleration.getStart(), selectedAcceleration.getEnd());
	}

	public void setLogData(LogFileData logFileData) {
		this.logFileData = logFileData;
	}

	public boolean isRpmBased() {
		return logFileData.isRpmBased();
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

		LoggedRunsEntry run = (LoggedRunsEntry) o;

		return id.equals(run.id);

	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public int compareTo(LoggedRunsEntry o) {
		return Integer.valueOf(this.index).compareTo(o.getIndex());
	}
}
