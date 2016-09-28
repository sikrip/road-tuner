package sikrip.roaddyno.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
	 * Maximum Number of deceleration log entries in order to decide that deceleration is occurring.
	 */
	private static final int DECELERATION_THRESHOLD = 3;

	/**
	 * Smooths the velocity values of the given log entries using the Local Regression Algorithm (Loess, Lowess).
	 *
	 * @param rawEntries
	 * 		the raw log entries
	 * @return the log entries with the velocity values smoothed
	 */
	static List<LogEntry> smoothVelocity(List<LogEntry> rawEntries) {

		List<LogEntry> smoothedEntries = new ArrayList<>();

		RawValuesExtractor rawValuesExtractor = new RawValuesExtractor(rawEntries).invoke();
		double[] timeValues = rawValuesExtractor.getTimeValues();
		double[] rawVelocityValues = rawValuesExtractor.getRawVelocityValues();

		double[] smoothedRPMValues = new LoessInterpolator().smooth(timeValues, rawVelocityValues);

		for (int i = 0; i < rawEntries.size(); i++) {
			LogEntry logEntryCopy = rawEntries.get(i).getCopy();
			logEntryCopy.getVelocity().setValue(smoothedRPMValues[i]);
			smoothedEntries.add(logEntryCopy);
		}

		return smoothedEntries;
	}

	//TODO review this method, currently not used because it does not work well
	static AccelerationBounds getAccelerationBoundsByRPM(List<LogEntry> rawEntries) {

		final int logSize = rawEntries.size();

		RawValuesExtractor rawValuesExtractor = new RawValuesExtractor(rawEntries).invoke();
		double[] timeValues = rawValuesExtractor.getTimeValues();
		double[] rawRPMValues = rawValuesExtractor.getRawVelocityValues();

		double[] rpmDS = getDSYValues(timeValues, rawRPMValues, 1);

		int start = findIndexOfFirstGreaterThanMean(rpmDS, 0);

		int end = findIndexOfMin(rpmDS, start);

		List<LogEntry> smoothedEntries = smoothVelocity(rawEntries.subList(0, end));

		rawValuesExtractor = new RawValuesExtractor(smoothedEntries).invoke();
		timeValues = rawValuesExtractor.getTimeValues();
		rawRPMValues = rawValuesExtractor.getRawVelocityValues();

		rpmDS = getDSYValues(timeValues, rawRPMValues, 1);

		start = findIndexOfFirstGreaterThanMean(rpmDS, 0);

		start = Math.min(start, logSize);
		end = Math.min(end, logSize);
		return new AccelerationBounds(start, end);
	}

	static AccelerationBounds getAccelerationBoundsBySpeed(List<LogEntry> rawEntries, int offset) {

		final int logSize = rawEntries.size();

		RawValuesExtractor rawValuesExtractor = new RawValuesExtractor(rawEntries).invoke();
		double[] timeValues = rawValuesExtractor.getTimeValues();
		double[] rawSpeedValues = rawValuesExtractor.getRawVelocityValues();

		// Find the first derivative of the the time/speed function
		// Positive number in this array indicate acceleration; negative numbers deceleration
		double[] speedDS = getDSYValues(timeValues, rawSpeedValues, 1);

		// The index of the first value of the 1st derivative that is greater than the
		// mean value of all 1st derivative positive(acceleration) values is considered the start of the acceleration.
		int start = findIndexOfFirstGreaterThanMean(speedDS, offset);

		if (start == -1) {
			return null;
		}

		// The nth-negative value(after the start) of the 1st derivative indicates deceleration, so this is the end of the run
		// FIXME consider adding 1 in the end index here (the end should be non inclusive)
		int end = findNthNegativeIndex(speedDS, start, DECELERATION_THRESHOLD);

		start = Math.min(start, logSize);
		end = Math.min(end, logSize);
		return new AccelerationBounds(start, end);
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

		final double[] yDSValues = new double[yValues.length];
		final Set<Integer> interpolationErrorIndices = new HashSet<>();

		for (int i = 0; i < xValues.length; i++) {
			try {
				double xValue = xValues[i];
				DerivativeStructure xDS = new DerivativeStructure(1, 3, 0, xValue);
				DerivativeStructure yDS = completeF.value(xDS);
				yDSValues[i] = yDS.getPartialDerivative(order);
			} catch (Exception e) {
				// x value cannot be interpolated via the function
				interpolationErrorIndices.add(i);
				yDSValues[i] = Double.MIN_VALUE;
			}
		}

		// Fix invalid y values produced by x values that could not be interpolated via the function
		while (!interpolationErrorIndices.isEmpty()) {
			final Iterator<Integer> errorValuesIterator = interpolationErrorIndices.iterator();
			while (errorValuesIterator.hasNext()) {
				final int errorValueIndex = errorValuesIterator.next();
				if (errorValueIndex == 0) {
					yDSValues[errorValueIndex] = yDSValues[1];
				} else {
					yDSValues[errorValueIndex] = yDSValues[errorValueIndex - 1];
				}
				if (yDSValues[errorValueIndex] != Double.MIN_VALUE) {
					// if the value is fixed remove the index; otherwise it will be fixed in next iterations
					errorValuesIterator.remove();
				}
			}
		}

		return yDSValues;
	}

	/**
	 * Finds the index of the first value that is greater that the mean
	 * value of the positives within the provided array of values
	 * starting from the provided offset.
	 *
	 * @param values
	 * 		the array of values
	 * @param offset
	 * 		the offset within the array to start the operation from
	 * @return the index of the first value that is greater that the mean
	 * value of the positives
	 */
	private static int findIndexOfFirstGreaterThanMean(double[] values, int offset) {
		double mean = 0;
		int sumCount = 0;
		for (int i = offset; i < values.length; i++) {
			if (values[i] > 0) {
				mean += values[i];
				sumCount++;
			}
		}

		if (sumCount == 0) {
			// no positive
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

	/**
	 * Finds the index of the nth negative number in the provided array.
	 * If no n negatives exists the index of the n-1 negative will be returned and so on.
	 *
	 * @param values
	 * 		the array of values
	 * @param offset
	 * 		the offset within the array to start the operation from
	 * @param maxNegativeCount
	 * 		the requested number of negatives
	 * @return the index of the nth negative number in the provided array
	 */
	private static int findNthNegativeIndex(double[] values, int offset, int maxNegativeCount) {
		int negativeCount = 0;
		int lastNegativeIdx = -1;
		for (int i = offset; i < values.length; i++) {
			if (values[i] < 0) {
				lastNegativeIdx = i;
				negativeCount++;
			}
			if (negativeCount == maxNegativeCount) {
				return lastNegativeIdx;
			}
		}

		if (negativeCount != 0 && negativeCount < maxNegativeCount) {
			return lastNegativeIdx;
		} else {
			// no deceleration
			return values.length - 1;
		}
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
