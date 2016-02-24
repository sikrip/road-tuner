package sikrip.roaddyno.standalone;

import static sikrip.roaddyno.standalone.util.FontUtil.htmlText;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.swing.*;

import sikrip.roaddyno.model.RunInfo;
import sikrip.roaddyno.standalone.util.LayoutUtil;
import sikrip.roaddyno.standalone.util.UnitConverter;

final class RunInfoPanel extends JPanel implements RunInfo, KeyListener {

	private DecimalFormat df;

	private String runName;
	private final JTextField fgr = new JTextField("4.312");
	private final JTextField gr = new JTextField("1.310");
	private final JTextField tyreDiameter = new JTextField("528");
	private final JTextField tyreDiameterAlt = new JTextField();
	private final JTextField carWeight = new JTextField("905");
	private final JTextField carWeightAlt = new JTextField();
	private final JTextField occupantsWeight = new JTextField("85");
	private final JTextField occupantsWeightAlt = new JTextField();
	private final JTextField frontalArea = new JTextField("1.8");
	private final JTextField frontalAreaAlt = new JTextField();
	private final JTextField cd = new JTextField("0.34");

	RunInfoPanel() {
		createGui();

		setupListeners();

		df = new DecimalFormat("#.00");
		df.setRoundingMode(RoundingMode.UP);

		synchCarWeight();
		synchFrontalArea();
		synchOccupantsWeight();
		synchTyreDiameter();
	}

	private void createGui() {

		setLayout(new SpringLayout());

		JLabel label = new JLabel("FGR: ", JLabel.TRAILING);
		add(label);
		label.setLabelFor(fgr);
		add(fgr);
		add(Box.createHorizontalBox());
		add(Box.createHorizontalBox());

		label = new JLabel("Gear Ratio: ", JLabel.TRAILING);
		add(label);
		label.setLabelFor(gr);
		add(gr);
		add(Box.createHorizontalBox());
		add(Box.createHorizontalBox());

		label = new JLabel(htmlText("Tyre diam <strong>(mm)</strong>: "), JLabel.TRAILING);
		add(label);
		label.setLabelFor(tyreDiameter);
		add(tyreDiameter);
		label = new JLabel(htmlText("<strong> (inch)</strong>: "), JLabel.TRAILING);
		add(label);
		label.setLabelFor(tyreDiameterAlt);
		add(tyreDiameterAlt);

		label = new JLabel(htmlText("Car weight <strong>(kg)</strong>: "), JLabel.TRAILING);
		add(label);
		label.setLabelFor(carWeight);
		add(carWeight);
		label = new JLabel(htmlText(" <strong>(pounds)</strong>: "), JLabel.TRAILING);
		add(label);
		label.setLabelFor(carWeightAlt);
		add(carWeightAlt);

		label = new JLabel(htmlText("Occ weight <strong>(kg)</strong>: "), JLabel.TRAILING);
		add(label);
		label.setLabelFor(occupantsWeight);
		add(occupantsWeight);
		label = new JLabel(htmlText(" <strong>(pounds)</strong>: "), JLabel.TRAILING);
		add(label);
		label.setLabelFor(occupantsWeightAlt);
		add(occupantsWeightAlt);

		label = new JLabel(htmlText("Frontal area <strong>(m^2)</strong>: "), JLabel.TRAILING);
		add(label);
		label.setLabelFor(frontalArea);
		add(frontalArea);
		label = new JLabel(htmlText(" <strong>(Ft^2)</strong>: "), JLabel.TRAILING);
		add(label);
		label.setLabelFor(frontalAreaAlt);
		add(frontalAreaAlt);

		label = new JLabel(htmlText("Coeff of drag: "), JLabel.TRAILING);
		add(label);
		label.setLabelFor(cd);
		add(cd);
		add(Box.createHorizontalBox());
		add(Box.createHorizontalBox());

		//Lay out the panel.
		LayoutUtil.makeCompactGrid(this,
				7, 4, //rows, cols
				3, 3, //initX, initY
				3, 3);
	}

	private void setupListeners() {
		tyreDiameter.addKeyListener(this);
		tyreDiameterAlt.addKeyListener(this);
		carWeight.addKeyListener(this);
		carWeightAlt.addKeyListener(this);
		occupantsWeight.addKeyListener(this);
		occupantsWeightAlt.addKeyListener(this);
		frontalArea.addKeyListener(this);
		frontalAreaAlt.addKeyListener(this);
	}

	void setInfo(String name, Double fgr, Double gr, Double tyreDiameter, Double carWeight, Double occupantsWeight, Double fa, Double cd) {
		this.runName = name;
		setFgr(fgr);
		setGr(gr);
		setTyreDiameter(tyreDiameter);
		setCarWeight(carWeight);
		setOccupantsWeight(occupantsWeight);
		setFrontalArea(fa);
		setCd(cd);

		synchCarWeight();
		synchFrontalArea();
		synchOccupantsWeight();
		synchTyreDiameter();
	}

	void setFgr(Double fgr) {
		this.fgr.setText(String.valueOf(fgr));
	}

	void setGr(Double gr) {
		this.gr.setText(String.valueOf(gr));
	}

	void setTyreDiameter(Double tyreDiameter) {
		this.tyreDiameter.setText(String.valueOf(tyreDiameter));
	}

	void setCarWeight(Double carWeight) {
		this.carWeight.setText(String.valueOf(carWeight));
	}

	void setOccupantsWeight(Double occupantsWeight) {
		this.occupantsWeight.setText(String.valueOf(occupantsWeight));
	}

	void setFrontalArea(Double frontalArea) {
		this.frontalArea.setText(String.valueOf(frontalArea));
	}

	void setCd(Double cd) {
		this.cd.setText(String.valueOf(cd));
	}

	public Double getFinalGearRatio() {
		return Double.valueOf(fgr.getText());
	}

	public Double getGearRatio() {
		return Double.valueOf(gr.getText());
	}

	public Double getTyreDiameter() {
		return Double.valueOf(tyreDiameter.getText());
	}

	public Double getCarWeight() {
		return Double.valueOf(carWeight.getText());
	}

	public Double getOccupantsWeight() {
		return Double.valueOf(occupantsWeight.getText());
	}

	public Double getFrontalArea() {
		return Double.valueOf(frontalArea.getText());
	}

	public Double getCoefficientOfDrag() {
		return Double.valueOf(cd.getText());
	}

	public String getName() {
		return runName;
	}

	private String formatValue(Double value) {
		return df.format(value);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Object source = e.getSource();

		if (source.equals(tyreDiameter)) {
			synchTyreDiameter();
		} else if (source.equals(tyreDiameterAlt)) {
			try {
				Double value = Double.valueOf(tyreDiameterAlt.getText());
				tyreDiameter.setText(formatValue(UnitConverter.inchToMM(value)));
			} catch (NumberFormatException ex) {
				System.err.println(ex.getMessage());
			}
		} else if (source.equals(carWeight)) {
			synchCarWeight();
		} else if (source.equals(carWeightAlt)) {
			try {
				Double value = Double.valueOf(carWeightAlt.getText());
				carWeight.setText(formatValue(UnitConverter.poundsToKg(value)));
			} catch (NumberFormatException ex) {
				System.err.println(ex.getMessage());
			}
		} else if (source.equals(occupantsWeight)) {
			synchOccupantsWeight();
		} else if (source.equals(occupantsWeightAlt)) {
			try {
				Double value = Double.valueOf(occupantsWeightAlt.getText());
				occupantsWeight.setText(formatValue(UnitConverter.poundsToKg(value)));
			} catch (NumberFormatException ex) {
				System.err.println(ex.getMessage());
			}
		} else if (source.equals(frontalArea)) {
			synchFrontalArea();
		} else if (source.equals(frontalAreaAlt)) {
			try {
				Double value = Double.valueOf(frontalAreaAlt.getText());
				frontalArea.setText(formatValue(UnitConverter.sgFtToSqM(value)));
			} catch (NumberFormatException ex) {
				System.err.println(ex.getMessage());
			}
		}
	}

	private void synchFrontalArea() {
		try {
			Double value = Double.valueOf(frontalArea.getText());
			frontalAreaAlt.setText(formatValue(UnitConverter.sgMToSqFt(value)));
		} catch (NumberFormatException ex) {
			System.err.println(ex.getMessage());
		}
	}

	private void synchOccupantsWeight() {
		try {
			Double value = Double.valueOf(occupantsWeight.getText());
			occupantsWeightAlt.setText(formatValue(UnitConverter.kgToPounds(value)));
		} catch (NumberFormatException ex) {
			System.err.println(ex.getMessage());
		}
	}

	private void synchCarWeight() {
		try {
			Double value = Double.valueOf(carWeight.getText());
			carWeightAlt.setText(formatValue(UnitConverter.kgToPounds(value)));
		} catch (NumberFormatException ex) {
			System.err.println(ex.getMessage());
		}
	}

	private void synchTyreDiameter() {
		try {
			Double value = Double.valueOf(tyreDiameter.getText());
			tyreDiameterAlt.setText(formatValue(UnitConverter.mmToInch(value)));
		} catch (NumberFormatException ex) {
			System.err.println(ex.getMessage());
		}
	}
}
