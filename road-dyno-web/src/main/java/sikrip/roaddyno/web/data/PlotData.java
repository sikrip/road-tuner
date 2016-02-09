package sikrip.roaddyno.web.data;

import java.util.ArrayList;
import java.util.List;

public class PlotData {

    private String type = "serial";
    private String categoryField = "category";
    private Integer startDuration = 1;
    private CategoryAxis categoryAxis = new CategoryAxis();
    private List<TrendLines> trendLines = new ArrayList<>();
    private List<Graph> graphs = new ArrayList<>();
}
