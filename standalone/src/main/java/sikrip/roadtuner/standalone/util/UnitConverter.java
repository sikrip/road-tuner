package sikrip.roaddyno.standalone.util;

public final class UnitConverter {

	private UnitConverter() {
	}

	public static double mmToInch(double mm) {
		return mm * 0.0393700787;
	}

	public static double inchToMM(double inch) {
		return inch * 25.4;
	}

	public static double kgToPounds(double kg) {
		return kg * 2.20462262;
	}

	public static double poundsToKg(double pounds) {
		return pounds * 0.45359237;
	}

	public static double sgFtToSqM(double sqft) {
		return sqft * 0.09290304;
	}

	public static double sgMToSqFt(double sqm) {
		return sqm * 10.7639104;
	}
}
