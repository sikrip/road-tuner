package sikrip.roaddyno.web.logger;

import java.text.DecimalFormat;

import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.model.LogValue;

public class AccelerationRun {

	private final int start;
	private final int end;

	private final LogEntry startEntry;
	private final LogEntry endEntry;

	public AccelerationRun(int start, int end, LogEntry startEntry, LogEntry endEntry) {
		this.start = start;
		this.end = end;
		this.startEntry = startEntry;
		this.endEntry = endEntry;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public LogEntry getStartEntry() {
		return startEntry;
	}

	public LogEntry getEndEntry() {
		return endEntry;
	}

	public double getVelocityDiff() {
		return endEntry.getVelocity().getValue() - startEntry.getVelocity().getValue();
	}

	@Override
	public String toString() {
		DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0.00" );
		StringBuilder builder = new StringBuilder()
				.append(startEntry.getVelocity().getValue())
				.append(startEntry.getVelocity().getUnit())
				.append(" - ")
				.append(endEntry.getVelocity().getValue())
				.append(endEntry.getVelocity().getUnit())
				.append(", duration ")
				.append(decimalFormat.format(endEntry.getTime().getValue() - startEntry.getTime().getValue()))
				.append(startEntry.getTime().getUnit());

		LogValue<Double> startHeight = startEntry.get("height");
		if (startHeight != null) {
			double heightDiff = startHeight.getValue() - endEntry.get("height").getValue();
			builder.append(", height diff: ")
					.append(decimalFormat.format(heightDiff))
					.append(startHeight.getUnit());
			if(heightDiff>0) {
				builder.append(" downhill");
			}else {
				builder.append(" uphill");
			}
		}
		return builder.toString();
	}
}
