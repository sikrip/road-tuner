package sikrip.roadtuner.engine.wottuner;

import java.io.IOException;
import java.util.List;
import sikrip.roadtuner.model.LogEntry;
import sikrip.roadtuner.model.WotTuneResult;
import sikrip.roadtuner.model.WotTunerProperties;

/**
 * Given a csv log from PowerTune, creates a map of logged AFR to be used for manual fine-tuning the base fuel map.
 */
public class WotTuner {

    private static final String DECIMAL_FORMAT = "%6.3f";

    public static WotTuneResult analyze(List<sikrip.roadtuner.model.LogEntry> logEntries, double[][] currentFuelMap, WotTunerProperties wotTunerProperties) throws IOException {
        final double[][] loggedAfr = new double[wotTunerProperties.getFuelTableSize()][wotTunerProperties.getFuelTableSize()];
        final int[][] loggedAfrSample = new int[wotTunerProperties.getFuelTableSize()][wotTunerProperties.getFuelTableSize()];
        double wotStart = 0;
        boolean underWOT = false;
        for (final LogEntry logEntry : logEntries) {
            final boolean underWotNow = logEntry.getTps().getValue() >= wotTunerProperties.getWotVolts();
            if (underWotNow) {
                if (!underWOT) {
                    // Start of wot
                    wotStart = logEntry.getTime().getValue();
                    underWOT = true;
                }
            } else {
                // End of wot
                wotStart = 0;
                underWOT = false;
            }
            if (underWOT) {
                if (logEntry.getTime().getValue() - wotStart >= wotTunerProperties.getAccelEnrichSeconds()) {
                    // fuel enrichment done
                    int row = logEntry.get(wotTunerProperties.getLoadIdxHeader()).getValue().intValue();
                    int col = logEntry.get(wotTunerProperties.getRpmIdxHeader()).getValue().intValue();
                    final double currentAvgSum = loggedAfr[row][col] * loggedAfrSample[row][col];
                    loggedAfrSample[row][col]++;
                    loggedAfr[row][col] = (currentAvgSum + logEntry.get(wotTunerProperties.getAfrHeader()).getValue()) / loggedAfrSample[row][col];
                }
            }
        }

        final double[][] newFuelMap = new double[wotTunerProperties.getFuelTableSize()][wotTunerProperties.getFuelTableSize()];
        final double[][] fuelMapDiff = new double[wotTunerProperties.getFuelTableSize()][wotTunerProperties.getFuelTableSize()];
        for (int i = 0; i < loggedAfr.length; i++) {
            for (int j = 0; j < loggedAfr[i].length; j++) {
                if (loggedAfrSample[i][j] >= wotTunerProperties.getMinNumberOfSamples()) {
                    final double newFuelValue = (loggedAfr[i][j] / wotTunerProperties.getWotTargetAfr()) * currentFuelMap[i][j];
                    newFuelMap[i][j] = newFuelValue;
                } else {
                    newFuelMap[i][j] = currentFuelMap[i][j];
                }
                fuelMapDiff[i][j] = newFuelMap[i][j] - currentFuelMap[i][j];
            }
        }

        return WotTuneResult.builder()
            .loggedAFRTable(loggedAfr)
            .newFuelMap(newFuelMap)
            .fuelMapDiff(fuelMapDiff)
            .build();
    }
}
