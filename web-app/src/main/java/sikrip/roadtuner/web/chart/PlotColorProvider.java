package sikrip.roadtuner.web.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PlotColorProvider {

	private final static String[] PLOT_COLORS = { "#FF0000", "#0000FF", "#008000", "#808080", "#000000" };
	private final List<String> availableColors = new ArrayList<>();

	public PlotColorProvider() {
		reset();
	}

	public void reset() {
		availableColors.clear();
		Collections.addAll(availableColors, PLOT_COLORS);
	}

	public String pop() {
		if (availableColors.isEmpty()) {
			return null;
		}
		return availableColors.remove(0);
	}

	public void push(String color) {
		availableColors.add(color);
	}

	public int size() {
		return PLOT_COLORS.length;
	}
}
