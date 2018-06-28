package sikrip.roaddyno.web.controller;

import org.springframework.web.multipart.MultipartFile;
import sikrip.roaddyno.engine.DynoSimulationResult;
import sikrip.roaddyno.engine.DynoSimulator;
import sikrip.roaddyno.engine.SimulationException;
import sikrip.roaddyno.model.InvalidLogFileException;
import sikrip.roaddyno.web.chart.PlotColorProvider;
import sikrip.roaddyno.web.logger.LogFileReader;
import sikrip.roaddyno.web.model.RunData;
import sikrip.roaddyno.web.model.RunPlot;
import sikrip.roaddyno.web.model.VehicleData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Holds a collection of {@link RunPlot}s.
 */
final class RunPlotCollection {

	private static final int MAX_RUNS = 5;

	private final Map<String, RunPlot> loggedRuns = new HashMap<>();

	private final PlotColorProvider colorProvider = new PlotColorProvider();

	private final VehicleData commonVehicleData = new VehicleData();

	private int nextRunIndex = 0;

	boolean isEmpty() {
		return loggedRuns.isEmpty();
	}

	boolean canAddRun() {
		return loggedRuns.size() < MAX_RUNS;
	}

	void add(RunPlot runPlot, MultipartFile file) throws InvalidLogFileException {
		final RunData runData = LogFileReader.readLog(file);

		if(runData.getWotRunBounds().isEmpty()){
			throw new InvalidLogFileException("No WOT runs detected in the loaded file.");
		}else {
			runPlot.setIndex(nextRunIndex++);
			runPlot.setSelectedAccelerationIdx(0);
			runPlot.setLogData(runData);
			runPlot.setRunName(file.getOriginalFilename());
			runPlot.updateFrom(commonVehicleData);
			runPlot.setColor(colorProvider.pop());
			loggedRuns.put(runPlot.getId(), runPlot);
		}
	}

	void update(RunPlot updatedEntry) throws SimulationException {

		final RunPlot existingEntry = get(updatedEntry.getId());

		if (existingEntry == null) {
			throw new RuntimeException(String.format("Run with id %s not found", updatedEntry.getId()));
		} else if (!existingEntry.hasWOTRuns()) {
			throw new SimulationException("No WOT runs exist in the loaded file.");
		} else {
			existingEntry.setRunName(updatedEntry.getRunName());
			existingEntry.setFinalGearRatio(updatedEntry.getFinalGearRatio());
			existingEntry.setGearRatio(updatedEntry.getGearRatio());
			existingEntry.setTyreDiameter(updatedEntry.getTyreDiameter());
			existingEntry.setCarWeight(updatedEntry.getCarWeight());
			existingEntry.setOccupantsWeight(updatedEntry.getOccupantsWeight());
			existingEntry.setFrontalArea(updatedEntry.getFrontalArea());
			existingEntry.setCoefficientOfDrag(updatedEntry.getCoefficientOfDrag());
			existingEntry.setSelectedAccelerationIdx(updatedEntry.getSelectedAccelerationIdx());
			existingEntry.setAuxiliaryPlotFieldA(updatedEntry.getAuxiliaryPlotFieldA());
			existingEntry.setAuxiliaryPlotFieldB(updatedEntry.getAuxiliaryPlotFieldB());

			commonVehicleData.updateFromRunInfo(updatedEntry);

			// (re)run the dyno
			final DynoSimulationResult dynoResult;
			if (existingEntry.isRpmBased()) {
				dynoResult = DynoSimulator.runByRPM(existingEntry.getSelectedLogEntries(),
						existingEntry.getFinalGearRatio(),
						existingEntry.getGearRatio(),
						existingEntry.getTyreDiameter(),
						existingEntry.getCarWeight(),
						existingEntry.getOccupantsWeight(),
						existingEntry.getFrontalArea(),
						existingEntry.getCoefficientOfDrag());

			} else {
				dynoResult = DynoSimulator.runBySpeed(existingEntry.getSelectedLogEntries(),
						existingEntry.getFinalGearRatio(),
						existingEntry.getGearRatio(),
						existingEntry.getTyreDiameter(),
						existingEntry.getCarWeight(),
						existingEntry.getOccupantsWeight(),
						existingEntry.getFrontalArea(),
						existingEntry.getCoefficientOfDrag());
			}
			existingEntry.setResult(dynoResult);
		}
	}

	void delete(String id) {
		final RunPlot runPlot = get(id);
		if (runPlot == null) {
			throw new RuntimeException(String.format("Run with id %s not found", id));
		} else {
			colorProvider.push(runPlot.getColor());
			loggedRuns.remove(id);
		}
	}

	RunPlot get(String id) {
		return loggedRuns.get(id);
	}

	List<RunPlot> getRunsToPlot() {
		return loggedRuns.values().stream().filter(RunPlot::isActive).collect(Collectors.toList());
	}

	Set<String> getAdditionalFields() {
		return loggedRuns.values()
				.stream()
				.map(r -> r.getRunData().getLogEntries().get(0).getDataKeys())
				.reduce(new HashSet<>(), (s1, s2) -> {
					Set<String> sum = new HashSet<>(s1);
					sum.addAll(s2);
					return sum;
				});
	}

	Set<String> getAllAuxiliaryPlotFields() {
		return loggedRuns.values()
		  	.stream()
			.map(r -> {
				Set<String> auxiliaryRuns = new HashSet<>();
				if (r.getAuxiliaryPlotFieldA() != null && !"".equals(r.getAuxiliaryPlotFieldA())) {
					auxiliaryRuns.add(r.getAuxiliaryPlotFieldA());
				}
				if (r.getAuxiliaryPlotFieldB() != null && !"".equals(r.getAuxiliaryPlotFieldB())) {
					auxiliaryRuns.add(r.getAuxiliaryPlotFieldB());
				}
				return auxiliaryRuns;
			})
		  	.reduce(new HashSet<>(), (s1, s2) -> {
			 	Set<String> sum = new HashSet<>(s1);
			 	sum.addAll(s2);
			 	return sum;
		  	});
	}

	List<RunPlot> getRuns() {
		return new ArrayList<>(loggedRuns.values());
	}

	void activate(String id, boolean active) {
		final RunPlot runPlot = get(id);
		if (runPlot == null) {
			throw new RuntimeException(String.format("Run with id %s not found.", id));
		} else {
			runPlot.setActive(active);
		}
	}

	void clear() {
		loggedRuns.clear();
		colorProvider.reset();
	}
}
