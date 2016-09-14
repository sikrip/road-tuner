package sikrip.roaddyno.engine;

import java.util.ArrayList;
import java.util.Arrays;
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

		double[] timeValues = new double[rawEntries.size()];
		double[] rawRPMValues = new double[rawEntries.size()];

		for (int i = 0; i < rawEntries.size(); i++) {
			LogEntry logEntry = rawEntries.get(i);
			timeValues[i] = logEntry.getTime().getValue();
			rawRPMValues[i] = logEntry.getRpm().getValue();
		}
		final double[] unitWeights = new double[timeValues.length];
		Arrays.fill(unitWeights, 0.1);
		double[] smoothedRPMValues = new LoessInterpolator().smooth(timeValues, rawRPMValues, unitWeights);

		for (int i = 0; i < rawEntries.size(); i++) {
			LogEntry logEntryCopy = rawEntries.get(i).getCopy();
			logEntryCopy.getRpm().setValue(smoothedRPMValues[i]);
			smoothedEntries.add(logEntryCopy);
		}

		return smoothedEntries;
	}

	static AccelerationBounds getMaxAccelerationBounds(List<LogEntry> rawEntries) {

		RawValuesExtractor rawValuesExtractor = new RawValuesExtractor(rawEntries).invoke();
		double[] timeValues = rawValuesExtractor.getTimeValues();
		double[] rawRPMValues = rawValuesExtractor.getRawRPMValues();

		// function to be differentiated
		UnivariateInterpolator interpolator = new SplineInterpolator();
		UnivariateFunction basicF = interpolator.interpolate(timeValues, rawRPMValues);

		// create a differentiator using 5 points and 0.01 step
		FiniteDifferencesDifferentiator differentiator = new FiniteDifferencesDifferentiator(5, 0.01);

		// create a new function that computes both the value and the derivatives
		// using DerivativeStructure
		UnivariateDifferentiableFunction completeF = differentiator.differentiate(basicF);

		double[] rpmDS = new double[rawRPMValues.length];

		for (int i = 0; i < timeValues.length; i++) {
			try {
				double timeValue = timeValues[i];
				DerivativeStructure xDS = new DerivativeStructure(1, 3, 0, timeValue);
				DerivativeStructure yDS = completeF.value(xDS);
				rpmDS[i] = yDS.getPartialDerivative(1);
			} catch (Exception e) {
				rpmDS[i] = Double.MIN_VALUE;
			}
		}

		clearInvalidValues(rpmDS);

		double target = findMax(rpmDS) * 0.7;
		int start = -1;
		for (int i = 0; i < rpmDS.length; i++) {
			if (rpmDS[i] > target) {
				start = i;
				break;
			}
		}

		int end = findIndexOfMin(rpmDS, start+1);

		List<LogEntry> smoothedEntries = smoothRPM(rawEntries.subList(0, end));

		rawValuesExtractor = new RawValuesExtractor(smoothedEntries).invoke();
		timeValues = rawValuesExtractor.getTimeValues();
		rawRPMValues = rawValuesExtractor.getRawRPMValues();

		// function to be differentiated
		interpolator = new SplineInterpolator();
		basicF = interpolator.interpolate(timeValues, rawRPMValues);

		// create a differentiator using 5 points and 0.01 step
		differentiator = new FiniteDifferencesDifferentiator(5, 0.01);

		// create a new function that computes both the value and the derivatives
		// using DerivativeStructure
		completeF = differentiator.differentiate(basicF);

		rpmDS = new double[rawRPMValues.length];

		for (int i = 0; i < timeValues.length; i++) {
			try {
				double timeValue = timeValues[i];
				DerivativeStructure xDS = new DerivativeStructure(1, 3, 0, timeValue);
				DerivativeStructure yDS = completeF.value(xDS);
				rpmDS[i] = yDS.getPartialDerivative(1);
			} catch (Exception e) {
				rpmDS[i] = Double.MIN_VALUE;
			}
		}

		clearInvalidValues(rpmDS);

		double max = findMax(rpmDS) * 0.7;
		start = -1;
		for (int i = 0; i < rpmDS.length; i++) {
			if (rpmDS[i] > max) {
				start = i;
				break;
			}
		}


		return new AccelerationBounds(start, end);
	}

	private static void clearInvalidValues(double[] rpmDS) {
		for (int i = 0; i < rpmDS.length; i++) {
			if (rpmDS[i] == Double.MIN_VALUE) {
				if (i == 0) {
					rpmDS[i] = rpmDS[1];
				} else {
					rpmDS[i] = rpmDS[i - 1];
				}
			}
		}
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

	private static class RawValuesExtractor {

		private List<LogEntry> rawEntries;
		private double[] timeValues;
		private double[] rawRPMValues;

		public RawValuesExtractor(List<LogEntry> rawEntries) {
			this.rawEntries = rawEntries;
		}

		public double[] getTimeValues() {
			return timeValues;
		}

		public double[] getRawRPMValues() {
			return rawRPMValues;
		}

		public RawValuesExtractor invoke() {
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
