package sikrip.roadtuner.engine.wottuner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sikrip.roadtuner.model.LogEntry;
import sikrip.roadtuner.model.WotTunerProperties;

/**
 * Given a csv log from PowerTune, creates a map of logged AFR to be used for manual fine-tuning the base fuel map.
 */
public class WotTuner {

    private static final String DECIMAL_FORMAT = "%6.3f";
    private static final String NEW_FUEL_MAP_FILE = "./new-fuel.map";

    public static String run(List<sikrip.roadtuner.model.LogEntry> logEntries, double[][] currentFuelMap, WotTunerProperties wotTunerProperties) throws IOException {
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
                    int col = logEntry.get(wotTunerProperties.getRpmIdxHeader()).getValue().intValue();
                    int row = logEntry.get(wotTunerProperties.getLoadIdxHeader()).getValue().intValue();
                    final double currentAvgSum =
                        loggedAfr[col][row] * loggedAfrSample[col][row];
                    loggedAfrSample[col][row]++;
                    loggedAfr[col][row] =
                        (currentAvgSum + logEntry.get(wotTunerProperties.getAfrHeader()).getValue()) /
                            loggedAfrSample[col][row];
                }
            }

        }

        System.out.println("\n========= Logged AFR ===========");
        for (int i = 0; i < loggedAfr.length; i++) {
            if (i==0) {
                printMapN_Values();
            }
            for (int j = 0; j < loggedAfr[i].length; j++) {
                if (j==0) {
                    printMapP_Value(i);
                }
                if (loggedAfrSample[i][j] >= minNumberOfSamples) {
                    System.out.printf(DECIMAL_FORMAT, loggedAfr[i][j]);
                } else {
                    System.out.printf(DECIMAL_FORMAT, 0.0);
                }
                if (j < loggedAfr[i].length - 1) {
                    System.out.print("\t");
                }
            }
            System.out.println();
        }
        final double[][] newFuelMap = new double[fuelTableSize][fuelTableSize];
        for (int i = 0; i < loggedAfr.length; i++) {
            for (int j = 0; j < loggedAfr[i].length; j++) {
                if (loggedAfrSample[i][j] >= minNumberOfSamples) {
                    final double newFuelValue = (loggedAfr[i][j] / wotTargetAfr) * currentFuelMap[i][j];
                    newFuelMap[i][j] = newFuelValue;
                } else {
                    newFuelMap[i][j] = currentFuelMap[i][j];
                }
            }
        }

        System.out.printf("\n========= New Fuel Map (also saved under %s) ===========\n", NEW_FUEL_MAP_FILE);
        try (BufferedWriter newFuelMapWriter = new BufferedWriter(new FileWriter(NEW_FUEL_MAP_FILE))) {
            for (int i = 0; i < newFuelMap.length; i++) {
                if (i==0) {
                    printMapN_Values();
                }
                for (int j = 0; j < newFuelMap[i].length; j++) {
                    if (j==0) {
                        printMapP_Value(i);
                    }
                    System.out.printf(DECIMAL_FORMAT, newFuelMap[i][j]);
                    newFuelMapWriter.write(String.format("%.3f", newFuelMap[i][j]));
                    if (j < newFuelMap[i].length - 1) {
                        System.out.print("\t");
                        newFuelMapWriter.write("\t");
                    }
                }
                System.out.println();
                newFuelMapWriter.write("\n");
            }
        }
        System.out.println("\n========= New-Old Map ===========");
        for (int i = 0; i < newFuelMap.length; i++) {
            if (i==0) {
                printMapN_Values();
            }
            for (int j = 0; j < newFuelMap[i].length; j++) {
                if (j==0) {
                    printMapP_Value(i);
                }
                System.out.printf(DECIMAL_FORMAT, newFuelMap[i][j] - currentFuelMap[i][j]);
                if (j < newFuelMap[i].length - 1) {
                    System.out.print("\t");
                }
            }
            System.out.println();
        }
    }

    private static void printMapP_Value(int i) {
        System.out.printf("%5d(%2d)", loadLabels[i], i + 1);
    }

    private static void printMapN_Values() {
        // each MAPP is 9 chars long
        System.out.print("         ");
        for(int j = 0; j < fuelTableSize; j++) {
            System.out.printf("%6d", rpmLabels[j]);
            if (j < fuelTableSize -1) {
                System.out.print("\t");
            }
        }
        System.out.println();
    }
}
