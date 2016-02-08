package sikrip.roaddyno.standalone;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

final class DynoRunAddDialog extends JDialog implements ActionListener {

	private final JFileChooser logFileChooser = new JFileChooser();
	private final JButton openFile = new JButton("Browse");

	private final JTextField runName = new JTextField();
	private final JTextField runFile = new JTextField();

	private DynoRunInfoPanel infoPanel = new DynoRunInfoPanel();
	private final JButton loadRun = new JButton("Load run");
	private final JButton cancel = new JButton("Cancel");

	private boolean userConfirmed;

	DynoRunAddDialog() {

		super(RoadDynoGui.getInstance(), true);

		setTitle("Load run");

		createGui();

		logFileChooser.setFileFilter(new FileNameExtensionFilter(
				"Megasquirt log files", "msl"));

		pack();
		setLocationRelativeTo(RoadDynoGui.getInstance());

		setupActionListeners();
	}

	boolean showDynoDialog() {
		clear();
		userConfirmed = false;
		setVisible(true);
		return userConfirmed;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openFile) {
			openFile();
		} else if (e.getSource() == loadRun) {
			userConfirmed = true;
			setVisible(false);
		} else if (e.getSource() == cancel) {
			userConfirmed = false;
			setVisible(false);
		}
	}

	String getRunFilePath() {
		return runFile.getText();
	}

	String getRunName() {
		return runName.getText();
	}

	double getFGR() {
		return infoPanel.getFinalGearRatio();
	}

	double getGearRatio() {
		return infoPanel.getGearRatio();
	}

	double getTyreDiameter() {
		return infoPanel.getTyreDiameter();
	}

	double getCarWeight() {
		return infoPanel.getCarWeight();
	}

	double getOccupantsWeight() {
		return infoPanel.getOccupantsWeight();
	}

	double getFrontalArea() {
		return infoPanel.getFrontalArea();
	}

	double getCD() {
		return infoPanel.getCoefficientOfDrag();
	}

	private void createGui() {
		JPanel contentPane = new JPanel();

		BoxLayout layout = new BoxLayout(contentPane, BoxLayout.PAGE_AXIS);
		contentPane.setLayout(layout);

		JPanel fieldPanel = new JPanel(new BorderLayout());
		fieldPanel.setBorder(BorderFactory.createTitledBorder("Log file"));
		contentPane.add(fieldPanel);
		fieldPanel.add(runFile, BorderLayout.CENTER);
		fieldPanel.add(openFile, BorderLayout.EAST);
		runFile.setEditable(false);

		fieldPanel = new JPanel(new BorderLayout());
		fieldPanel.setBorder(BorderFactory.createTitledBorder("Run name"));
		fieldPanel.add(runName);
		contentPane.add(fieldPanel);
		contentPane.add(infoPanel);

		infoPanel.setBorder(BorderFactory.createTitledBorder("Car/Run Info"));

		JPanel buttons = new JPanel();
		buttons.add(loadRun);
		buttons.add(cancel);
		contentPane.add(buttons);

		contentPane.setPreferredSize(new Dimension(360, 300));

		setContentPane(contentPane);
	}

	private void clear() {
		runFile.setText("");
		runName.setText("");
	}

	private void setupActionListeners() {
		openFile.addActionListener(this);
		loadRun.addActionListener(this);
		cancel.addActionListener(this);
	}

	private void openFile() {
		if (logFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			runFile.setText(logFileChooser.getSelectedFile().getAbsolutePath());
			runName.setText(logFileChooser.getSelectedFile().getName());
		}
	}
}
