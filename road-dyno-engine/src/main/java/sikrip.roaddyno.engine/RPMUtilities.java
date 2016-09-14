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
final class RPMUtilities {

	/**
	 * Smooths the RPM values of the given log entries using the Local Regression Algorithm (Loess, Lowess).
	 *
	 * @param rawEntries
	 * 		the raw log entries
	 * @return the log entries with the RPM values smoothed
	 */
	static List<LogEntry> smoothRPM(List<LogEntry> rawEntries) {

		List<LogEntry> smoothedEntries = new ArrayList<>();

		RawRPMValuesExtractor rawRPMValuesExtractor = new RawRPMValuesExtractor(rawEntries).invoke();
		double[] timeValues = rawRPMValuesExtractor.getTimeValues();
		double[] rawRPMValues = rawRPMValuesExtractor.getRawRPMValues();

		double[] smoothedRPMValues = new LoessInterpolator().smooth(timeValues, rawRPMValues);

		for (int i = 0; i < rawEntries.size(); i++) {
			LogEntry logEntryCopy = rawEntries.get(i).getCopy();
			logEntryCopy.getRpm().setValue(smoothedRPMValues[i]);
			smoothedEntries.add(logEntryCopy);
		}

		return smoothedEntries;
	}

	static AccelerationBounds getMaxAccelerationBounds(List<LogEntry> rawEntries) {

		final int logSize = rawEntries.size();

		RawRPMValuesExtractor rawRPMValuesExtractor = new RawRPMValuesExtractor(rawEntries).invoke();
		double[] timeValues = rawRPMValuesExtractor.getTimeValues();
		double[] rawRPMValues = rawRPMValuesExtractor.getRawRPMValues();

		double[] rpmDS = getDSYValues(timeValues, rawRPMValues, 1);

		double target = findMax(rpmDS) * 0.7;
		int start = -1;
		for (int i = 0; i < rpmDS.length; i++) {
			if (rpmDS[i] > target) {
				start = i;
				break;
			}
		}

		if (start >= logSize - 1) {
			throw new IllegalStateException("No valid acceleration exists in the provided log entries.");
		}

		int end = findIndexOfMin(rpmDS, start);

		List<LogEntry> smoothedEntries = smoothRPM(rawEntries.subList(0, end));

		rawRPMValuesExtractor = new RawRPMValuesExtractor(smoothedEntries).invoke();
		timeValues = rawRPMValuesExtractor.getTimeValues();
		rawRPMValues = rawRPMValuesExtractor.getRawRPMValues();

		rpmDS = getDSYValues(timeValues, rawRPMValues, 1);

		double max = findMax(rpmDS) * 0.7;
		start = -1;
		for (int i = 0; i < rpmDS.length; i++) {
			if (rpmDS[i] > max) {
				start = i;
				break;
			}
		}
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

	private static double findMax(double[] values) {
		double max = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] > max) {
				max = values[i];
			}
		}
		return max;
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

	private static class RawRPMValuesExtractor {

		private List<LogEntry> rawEntries;
		private double[] timeValues;
		private double[] rawRPMValues;

		public RawRPMValuesExtractor(List<LogEntry> rawEntries) {
			this.rawEntries = rawEntries;
		}

		public double[] getTimeValues() {
			return timeValues;
		}

		public double[] getRawRPMValues() {
			return rawRPMValues;
		}

		public RawRPMValuesExtractor invoke() {
			timeValues = new double[rawEntries.size()];
			rawRPMValues = new double[rawEntries.size()];

			for (int i = 0; i < rawEntries.size(); i++) {
				LogEntry logEntry = rawEntries.get(i);
				timeValues[i] = logEntry.getTime().getValue();
				rawRPMValues[i] = logEntry.getRpm().getValue();
			}
			return this;
		}
	}
}
