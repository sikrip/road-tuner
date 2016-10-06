package sikrip.roaddyno.web.model;

import sikrip.roaddyno.engine.RunInfo;

public class VehicleData {

    private Double finalGearRatio = 3.944;
    private Double gearRatio = 1.310;
    private Double tyreDiameter = 632.0;
    private Double carWeight = 1380.0;
    private Double occupantsWeight = 90.0;
    private Double frontalArea = 2.1;
    private Double coefficientOfDrag = 0.29;

    public void updateFromRunInfo(RunInfo runInfo){
        setFinalGearRatio(runInfo.getFinalGearRatio());
        setGearRatio(runInfo.getGearRatio());
        setTyreDiameter(runInfo.getTyreDiameter());
        setCarWeight(runInfo.getCarWeight());
        setOccupantsWeight(runInfo.getOccupantsWeight());
        setFrontalArea(runInfo.getFrontalArea());
        setCoefficientOfDrag(runInfo.getCoefficientOfDrag());
    }

    public Double getFinalGearRatio() {
        return finalGearRatio;
    }

    public void setFinalGearRatio(Double finalGearRatio) {
        this.finalGearRatio = finalGearRatio;
    }

    public Double getGearRatio() {
        return gearRatio;
    }

    public void setGearRatio(Double gearRatio) {
        this.gearRatio = gearRatio;
    }

    public Double getTyreDiameter() {
        return tyreDiameter;
    }

    public void setTyreDiameter(Double tyreDiameter) {
        this.tyreDiameter = tyreDiameter;
    }

    public Double getCarWeight() {
        return carWeight;
    }

    public void setCarWeight(Double carWeight) {
        this.carWeight = carWeight;
    }

    public Double getOccupantsWeight() {
        return occupantsWeight;
    }

    public void setOccupantsWeight(Double occupantsWeight) {
        this.occupantsWeight = occupantsWeight;
    }

    public Double getFrontalArea() {
        return frontalArea;
    }

    public void setFrontalArea(Double frontalArea) {
        this.frontalArea = frontalArea;
    }

    public Double getCoefficientOfDrag() {
        return coefficientOfDrag;
    }

    public void setCoefficientOfDrag(Double coefficientOfDrag) {
        this.coefficientOfDrag = coefficientOfDrag;
    }
}
