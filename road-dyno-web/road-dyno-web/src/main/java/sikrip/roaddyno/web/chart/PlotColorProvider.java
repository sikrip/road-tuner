package sikrip.roaddyno.web.chart;

import java.util.ArrayList;
import java.util.List;

public final class PlotColorProvider {

	private final List<String> colors = new ArrayList<>();

	public PlotColorProvider() {
		reset();
	}

	public void reset() {
		colors.clear();

		push("#FF0000");
		push("#0000FF");
		push("#008000");
		push("#808080");
		push("#000000");
		push("#00FFFF");
	}

	public final String pop() {
		if (colors.isEmpty()) {
			throw new IllegalArgumentException("No more color available");
		}
		return colors.remove(0);
	}

	private void push(String color) {
		colors.add(color);
	}
}
