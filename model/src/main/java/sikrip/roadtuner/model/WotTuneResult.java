package sikrip.roadtuner.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WotTuneResult {

    private final double[][] loggedAFRTable;
    private final double[][] newFuelMap;
    private final double[][] fuelMapDiff;

}
