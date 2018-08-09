package sikrip.roadtuner.web.model;

import sikrip.roadtuner.model.RunInfo;

public class VehicleData {

    private Double finalGearRatio;
    private Double gearRatio;
    private Double tyreDiameter;
    private Double carWeight;
    private Double occupantsWeight;
    private Double frontalArea = 1.7; // sqm
    private Double coefficientOfDrag = 0.31;

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
