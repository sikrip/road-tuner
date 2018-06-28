package sikrip.roaddyno.web.model;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import sikrip.roaddyno.engine.DynoSimulationEntry;
import sikrip.roaddyno.engine.DynoSimulationResult;
import sikrip.roaddyno.engine.RunInfo;
import sikrip.roaddyno.model.LogEntry;

/**
 * Wraps all related info for a run plot.
 * Includes the raw log entries, the simulation result, the name the identifier and color.
 */
public final class RunPlot implements RunInfo, Comparable<RunPlot> {

	/**
	 * The index of this run.
	 */
	private int index;

	/**
	 * The id of the run.
	 */
	private String id;

	/**
	 * The name of the run.
	 */
	private String runName;

	/**
	 * The color of the run.
	 */
	private String color;

	/**
	 * Flag indicating if this run is active or not.
	 */
	private boolean active = true;

	/**
	 * The simulation result of the run.
	 */
	private DynoSimulationResult result;

	/**
	 * Contains the read log file data along with possible WOT runs within these data.
	 */
	private RunData runData;

	/**
	 * The selected WOT run to be used for the dyno simulation.
	 */
	private int selectedAccelerationIdx;

	/**
	 * A collection of fields to plot in auxiliary charts.
	 */
	private Set<String> auxiliaryPlotFields;

	private Double finalGearRatio;
	private Double gearRatio;
	private Double tyreDiameter;
	private Double carWeight;
	private Double occupantsWeight;
	private Double frontalArea; // sqm
	private Double coefficientOfDrag;

	public RunPlot() {
		id = UUID.randomUUID().toString();
	}

	public Set<String> getAuxiliaryPlotFields() {
		return auxiliaryPlotFields;
	}

	public void setAuxiliaryPlotFields(Set<String> auxiliaryPlotFields) {
		this.auxiliaryPlotFields = auxiliaryPlotFields;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setRunName(String runName) {
		this.runName = runName;
	}

	public String getRunName() {
		return runName;
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
		return getRunName();
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

	public List<WOTRunBounds> getAccelerations() {
		return runData.getWotRunBounds();
	}

	public void updateFrom(VehicleData vehicleData) {
		setFinalGearRatio(vehicleData.getFinalGearRatio());
		setGearRatio(vehicleData.getGearRatio());
		setTyreDiameter(vehicleData.getTyreDiameter());
		setCarWeight(vehicleData.getCarWeight());
		setOccupantsWeight(vehicleData.getOccupantsWeight());
		setFrontalArea(vehicleData.getFrontalArea());
		setCoefficientOfDrag(vehicleData.getCoefficientOfDrag());
	}

	public List<LogEntry> getSelectedLogEntries() {
		WOTRunBounds selectedAcceleration = runData.getWotRunBounds().get(selectedAccelerationIdx);
		return runData.getLogEntries().subList(selectedAcceleration.getStart(), selectedAcceleration.getEnd());
	}

	public boolean hasWOTRuns(){
		return !runData.getWotRunBounds().isEmpty();
	}

	public void setLogData(RunData runData) {
		this.runData = runData;
	}

	public RunData getRunData() {
		return runData;
	}

	public boolean isRpmBased() {
		return runData.isRpmBased();
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

		RunPlot run = (RunPlot) o;

		return id.equals(run.id);

	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public int compareTo(RunPlot o) {
		return Integer.compare(this.index, o.index);
	}
}
