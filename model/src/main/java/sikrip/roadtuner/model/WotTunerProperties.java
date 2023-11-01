package sikrip.roadtuner.model;

import lombok.Data;

@Data
public class WotTunerProperties {

    private int fuelTableSize = 20;
    private int[] rpmLabels = new int[]{800, 1100, 1500, 1900, 2350, 2800, 3250, 3700, 4150, 4600, 5050, 5500, 5700, 6100, 6500, 6900, 7300, 7700, 8100, 8400};
    private int[] loadLabels = new int[]{450, 900, 1400, 1900, 2450, 2950, 3400, 4100, 4800, 5500, 6200, 6900, 7600, 8300, 9000, 9700, 10400, 11100, 11800, 12500};
    private String separator="\t";
    private int linesToSkip=0;
    private String timeHeader="Time(S)";
    private String rpmHeader="EngRev";
    private String afrHeader="AN3-AN4 Wide Band";
    private String throttleHeader="VTA V";
    private String rpmIdxHeader="MAPN";
    private String loadIdxHeader="MAPP";
    private double wotVolts = 4.0;
    private double accelEnrichSeconds = 0.1;
    private int minNumberOfSamples = 3;
    private double wotTargetAfr = 13.0;
}
