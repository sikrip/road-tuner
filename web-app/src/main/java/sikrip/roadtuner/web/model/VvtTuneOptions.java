package sikrip.roadtuner.web.model;

public class VvtTuneOptions {

    private int rpmStep;
    private int startRpm;
    private int endRpm;

    public int getStartRpm() {
        return startRpm;
    }

    public void setStartRpm(int startRpm) {
        this.startRpm = startRpm;
    }

    public int getEndRpm() {
        return endRpm;
    }

    public void setEndRpm(int endRpm) {
        this.endRpm = endRpm;
    }

    public int getRpmStep() {
        return rpmStep;
    }

    public void setRpmStep(int rpmStep) {
        this.rpmStep = rpmStep;
    }
}
