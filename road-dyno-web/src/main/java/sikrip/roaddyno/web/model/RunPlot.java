package sikrip.roaddyno.web.model;

import sikrip.roaddyno.model.DynoSimulationEntry;
import sikrip.roaddyno.model.DynoSimulationResult;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.RunInfo;
import sikrip.roaddyno.model.WotRunBounds;

import java.util.List;
import java.util.UUID;

/**
 * Wraps all related info for a run plot.
 * Includes the raw log entries, the dyno simulation result, the name, the identifier and color.
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
	 * Flag indicating if the plot for this run is active or not.
	 */
	private boolean active = true;

	/**
	 * Flag indicating the user input data is filled or not.
	 */
	private boolean dataFilled = false;

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
	 * Field for the first auxiliary chart.
	 */
	private String auxiliaryPlotFieldA;

	/**
	 * Field for the second auxiliary chart.
	 */
	private String auxiliaryPlotFieldB;

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

	public boolean isDataFilled() {
		return dataFilled;
	}

	public void setDataFilled(boolean dataFilled) {
		this.dataFilled = dataFilled;
	}

	public String getAuxiliaryPlotFieldA() {
		return auxiliaryPlotFieldA;
	}

	public void setAuxiliaryPlotFieldA(String auxiliaryPlotFieldA) {
		this.auxiliaryPlotFieldA = auxiliaryPlotFieldA;
	}

	public String getAuxiliaryPlotFieldB() {
		return auxiliaryPlotFieldB;
	}

	public void setAuxiliaryPlotFieldB(String auxiliaryPlotFieldB) {
		this.auxiliaryPlotFieldB = auxiliaryPlotFieldB;
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

	public List<WotRunBounds> getAccelerations() {
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
		WotRunBounds selectedAcceleration = runData.getWotRunBounds().get(selectedAccelerationIdx);
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
