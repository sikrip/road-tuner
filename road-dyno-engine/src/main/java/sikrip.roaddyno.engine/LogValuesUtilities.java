package sikrip.roaddyno.engine;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

import sikrip.roaddyno.model.LogEntry;

/**
 * Smooths the RPM values of the given log entries using the Local Regression Algorithm (Loess, Lowess).
 */
final class LogValuesUtilities {

	/**
	 * Maximum Number of consecutive deceleration log entries in order to decide that deceleration is occurring.
	 */
	private static final int DECELERATION_THRESHOLD = 3;

	/**
	 * Smooths the RPM values of the given log entries using the Local Regression Algorithm (Loess, Lowess).
	 *
	 * @param rawEntries
	 * 		the raw log entries
	 * @return the log entries with the RPM values smoothed
	 */
	static List<LogEntry> smoothRPM(List<LogEntry> rawEntries) {

		List<LogEntry> smoothedEntries = new ArrayList<>();

		RawValuesExtractor rawValuesExtractor = new RawValuesExtractor(rawEntries).invoke();
		double[] timeValues = rawValuesExtractor.getTimeValues();
		double[] rawRPMValues = rawValuesExtractor.getRawVelocityValues();

		double[] smoothedRPMValues = new LoessInterpolator().smooth(timeValues, rawRPMValues);

		for (int i = 0; i < rawEntries.size(); i++) {
			LogEntry logEntryCopy = rawEntries.get(i).getCopy();
			logEntryCopy.getVelocity().setValue(smoothedRPMValues[i]);
			smoothedEntries.add(logEntryCopy);
		}

		return smoothedEntries;
	}

	static AccelerationBounds getAccelerationBoundsBySpeed(List<LogEntry> rawEntries, int offset) {

		final int logSize = rawEntries.size();

		RawValuesExtractor rawValuesExtractor = new RawValuesExtractor(rawEntries).invoke();
		double[] timeValues = rawValuesExtractor.getTimeValues();
		double[] rawSpeedValues = rawValuesExtractor.getRawVelocityValues();

		// Find the first derivative of the the time/speed function
		double[] speedDS = getDSYValues(timeValues, rawSpeedValues, 1);

		int start = findStart(speedDS, offset);

		if (start == -1) {
			return null;
		}

		// The first negative value(after the start) of the 1st derivative indicates deceleration, so this is the end of the run
		int end = findFirstNegative(speedDS, start, DECELERATION_THRESHOLD);

		if (end == -1) {
			// no negative value found, log entries do not contain deceleration
			end = rawSpeedValues.length;
		}

		return new AccelerationBounds(Math.min(start, logSize), Math.min(end, logSize));
	}

	static AccelerationBounds getAccelerationBoundsByRPM(List<LogEntry> rawEntries) {

		final int logSize = rawEntries.size();

		RawValuesExtractor rawValuesExtractor = new RawValuesExtractor(rawEntries).invoke();
		double[] timeValues = rawValuesExtractor.getTimeValues();
		double[] rawRPMValues = rawValuesExtractor.getRawVelocityValues();

		double[] rpmDS = getDSYValues(timeValues, rawRPMValues, 1);

		int start = findStart(rpmDS, 0);

		int end = findIndexOfMin(rpmDS, start);

		List<LogEntry> smoothedEntries = smoothRPM(rawEntries.subList(0, end));

		rawValuesExtractor = new RawValuesExtractor(smoothedEntries).invoke();
		timeValues = rawValuesExtractor.getTimeValues();
		rawRPMValues = rawValuesExtractor.getRawVelocityValues();

		rpmDS = getDSYValues(timeValues, rawRPMValues, 1);

		start = findStart(rpmDS, 0);

		return new AccelerationBounds(Math.min(start, logSize), Math.min(end, logSize));
	}

	/**
	 * Gets the y values of the derivative of the provided order for the given x-y values.
	 *
	 * @param xValues
	 * 		x values
	 * @param yValues
	 * 		y values
	 * @param order
	 * 		the order of the derivative
	 * @return the y values of the derivative
	 */
	private static double[] getDSYValues(double[] xValues, double[] yValues, int order) {

		// function to be differentiated
		UnivariateInterpolator interpolator = new SplineInterpolator();
		UnivariateFunction basicF = interpolator.interpolate(xValues, yValues);

		// create a differentiator using 5 points and 0.01 step
		FiniteDifferencesDifferentiator differentiator = new FiniteDifferencesDifferentiator(5, 0.01);

		// create a new function that computes both the value and the derivatives
		// using DerivativeStructure
		UnivariateDifferentiableFunction completeF = differentiator.differentiate(basicF);

		double[] yDSValues = new double[yValues.length];
		for (int i = 0; i < xValues.length; i++) {
			try {
				double xValue = xValues[i];
				DerivativeStructure xDS = new DerivativeStructure(1, 3, 0, xValue);
				DerivativeStructure yDS = completeF.value(xDS);
				yDSValues[i] = yDS.getPartialDerivative(order);
			} catch (Exception e) {
				// x value cannot be interpolated via the function
				yDSValues[i] = Double.MIN_VALUE;
			}
		}

		// fix invalid y values produced by x values that could not be interpolated via the function
		for (int i = 0; i < yDSValues.length; i++) {
			if (yDSValues[i] == Double.MIN_VALUE) {
				if (i == 0) {
					yDSValues[i] = yDSValues[1];
				} else {
					yDSValues[i] = yDSValues[i - 1];
				}
			}
		}

		return yDSValues;
	}

	private static int findStart(double[] values, int offset) {
		double mean = 0;
		int sumCount = 0;
		for (int i = offset; i < values.length; i++) {
			if (values[i] > 0) {
				mean += values[i];
				sumCount++;
			}
		}

		if (sumCount == 0) {
			// no acceleration
			return -1;
		}

		mean = mean / sumCount;

		for (int i = offset; i < values.length; i++) {
			if (values[i] > mean) {
				return i;
			}
		}
		return -1;
	}

	private static int findFirstNegative(double[] values, int offset, int nthNegative) {
		int negativeCount = 0;
		int lastNegative = 1;
		for (int i = offset; i < values.length; i++) {
			if (values[i] < 0) {
				lastNegative = i;
				negativeCount++;
			}
			if (negativeCount == nthNegative) {
				return lastNegative;
			}
		}
		if (negativeCount < nthNegative) {
			return lastNegative;
		}
		return -1;
	}

	private static int findIndexOfMin(double[] values, int idxFrom) {
		double min = values[idxFrom];
		int minIdx = idxFrom;
		for (int i = idxFrom + 1; i < values.length; i++) {
			if (values[i] < min) {
				min = values[i];
				minIdx = i;
			}
		}
		return minIdx;
	}

	private static double[] smooth(double[] values) {
		final int SAMPLES = 5;
		final double[] smoothed = new double[values.length];

		double sum = 0;
		for (int i = 0; i < values.length; i++) {
			if (i > 0 && i % SAMPLES == 0) {
				double value = sum / SAMPLES;
				for (int j = i - SAMPLES; j < i; j++) {
					smoothed[j] = value;
				}
				sum = 0;
			} else {
				sum += values[i];
			}
		}
		return smoothed;
	}

	private static class RawValuesExtractor {

		private List<LogEntry> rawEntries;
		private double[] timeValues;
		private double[] rawVelocityValues;

		public RawValuesExtractor(List<LogEntry> rawEntries) {
			this.rawEntries = rawEntries;
		}

		public double[] getTimeValues() {
			return timeValues;
		}

		public double[] getRawVelocityValues() {
			return rawVelocityValues;
		}

		public RawValuesExtractor invoke() {
			timeValues = new double[rawEntries.size()];
			rawVelocityValues = new double[rawEntries.size()];

			for (int i = 0; i < rawEntries.size(); i++) {
				LogEntry logEntry = rawEntries.get(i);
				timeValues[i] = logEntry.getTime().getValue();
				rawVelocityValues[i] = logEntry.getVelocity().getValue();
			}
			return this;
		}
	}
}
