/*
 *   Copyright (c) 2005-2012, EFI Analytics & Phil Tobin. All Rights Reserved.
 *
 *  This software is the confidential and proprietary information of EFI Analytics.
 *  ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Ideas And Solutions.
 *
 *  EFI Analytics grants the right to distribute this software as
 *  distributed by EFI Analytics and only with a legal agreement
 *  Distribution with any commercial
 *  product or for profit or benefit to any commercial entity must be covered
 *  by and agreement with EFI Analytics or Philip S Tobin.
 *
 *  For questions or additional information contact:
 *  Phil Tobin
 *  EFI Analytics
 *  p_tobin@yahoo.com
 *
 */

package exampletunerstudioplugin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import com.efiAnalytics.plugin.ecu.ControllerAccess;
import com.efiAnalytics.plugin.ecu.MathException;

/**
 * @author Philip Tobin
 */
public class ExpressionEvalSample extends JPanel {

	ControllerAccess controllerAccess = null;
	JTextField txtExpression = new JTextField("nCylinders * rpm", 40);
	JLabel lblResult = new JLabel();
	JButton btnEvaluate = new JButton("Evaluate >>");
	DecimalFormat df = null;

	public ExpressionEvalSample() {
		setBorder(BorderFactory.createTitledBorder("Evaluate any Expression using Parameters or OutputChannels"));
		setLayout(new BorderLayout(4, 4));
		add(BorderLayout.CENTER, txtExpression);
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new BorderLayout());
		btnEvaluate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				evaluate();
			}
		});
		eastPanel.add(BorderLayout.WEST, btnEvaluate);

		lblResult.setPreferredSize(new Dimension(55, 18));
		eastPanel.add(BorderLayout.CENTER, lblResult);
		add(BorderLayout.EAST, eastPanel);

	}

	private void evaluate() {
		String mainConfig = controllerAccess.getEcuConfigurationNames()[0];
		try {
			double result = controllerAccess.evaluateExpression(mainConfig, txtExpression.getText());
			lblResult.setText(getFormatter().format(result));
		} catch (MathException ex) {
			Logger.getLogger(ExpressionEvalSample.class.getName()).log(Level.SEVERE, null, ex);
			JOptionPane.showMessageDialog(btnEvaluate, ex.getMessage());
		}
	}

	public void init(ControllerAccess ca) {
		this.controllerAccess = ca;
	}

	private DecimalFormat getFormatter() {
		if (df == null) {
			df = (DecimalFormat) DecimalFormat.getInstance();
			df.setMaximumFractionDigits(4);
		}
		return df;
	}

}
