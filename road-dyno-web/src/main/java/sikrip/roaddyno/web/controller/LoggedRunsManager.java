package sikrip.roaddyno.web.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

import sikrip.roaddyno.engine.DynoSimulationResult;
import sikrip.roaddyno.engine.DynoSimulator;
import sikrip.roaddyno.engine.SimulationException;
import sikrip.roaddyno.model.InvalidLogFileException;
import sikrip.roaddyno.web.chart.PlotColorProvider;
import sikrip.roaddyno.web.logger.LogFileReader;
import sikrip.roaddyno.web.model.LogFileData;
import sikrip.roaddyno.web.model.LoggedRunsEntry;
import sikrip.roaddyno.web.model.VehicleData;

final class LoggedRunsManager {

	private static final int MAX_RUNS = 5;

	private final Set<LoggedRunsEntry> loggedRuns = new TreeSet<>();

	private final PlotColorProvider colorProvider = new PlotColorProvider();

	private final VehicleData vehicleData = new VehicleData();

	boolean isEmpty() {
		return loggedRuns.isEmpty();
	}

	boolean canAddRun() {
		return loggedRuns.size() < MAX_RUNS;
	}

	void add(LoggedRunsEntry loggedRun, MultipartFile file) throws InvalidLogFileException {
		final LogFileData logFileData = LogFileReader.readLog(file);

		loggedRun.setIndex(loggedRuns.size());
		loggedRun.setSelectedAccelerationIdx(0);
		loggedRun.setLogData(logFileData);
		loggedRun.setName(file.getOriginalFilename());
		loggedRun.updateFrom(vehicleData);

		loggedRuns.add(loggedRun);
	}

	void update(LoggedRunsEntry updatedEntry) throws SimulationException {

		final LoggedRunsEntry existingEntry = get(updatedEntry.getId());

		if (existingEntry == null) {
			throw new RuntimeException(String.format("Run with id %s not found", updatedEntry.getId()));
		} else {
			existingEntry.setFinalGearRatio(updatedEntry.getFinalGearRatio());
			existingEntry.setGearRatio(updatedEntry.getGearRatio());
			existingEntry.setTyreDiameter(updatedEntry.getTyreDiameter());
			existingEntry.setCarWeight(updatedEntry.getCarWeight());
			existingEntry.setOccupantsWeight(updatedEntry.getOccupantsWeight());
			existingEntry.setFrontalArea(updatedEntry.getFrontalArea());
			existingEntry.setCoefficientOfDrag(updatedEntry.getCoefficientOfDrag());
			existingEntry.setSelectedAccelerationIdx(updatedEntry.getSelectedAccelerationIdx());

			if (existingEntry.getColor() == null) {
				// set color only the first time
				existingEntry.setColor(colorProvider.pop());
			}

			vehicleData.updateFromRunInfo(updatedEntry);

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
		final LoggedRunsEntry loggedRunsEntry = get(id);
		if (loggedRunsEntry == null) {
			throw new RuntimeException(String.format("Run with id %s not found", id));
		} else {
			colorProvider.push(loggedRunsEntry.getColor());
			loggedRuns.remove(loggedRunsEntry);
		}
	}

	LoggedRunsEntry get(String id) {
		final Optional<LoggedRunsEntry> runInfo = loggedRuns.stream().filter(r -> id.equals(r.getId())).findFirst();
		if (runInfo.isPresent()) {
			return runInfo.get();
		}
		return null;
	}

	List<LoggedRunsEntry> getRunsToPlot() {
		clearInvalidEntries();
		return loggedRuns.stream().filter(LoggedRunsEntry::isActive).collect(Collectors.toList());
	}

	public Set<LoggedRunsEntry> getRuns() {
		return loggedRuns;
	}

	void activate(String id, boolean active) {
		final LoggedRunsEntry loggedRunsEntry = get(id);
		if (loggedRunsEntry == null) {
			throw new RuntimeException(String.format("Run with id %s not found.", id));
		} else {
			loggedRunsEntry.setActive(active);
		}
	}

	void clear() {
		loggedRuns.clear();
		colorProvider.reset();
		//TODO clear vehicleData?
	}

	private void clearInvalidEntries() {
		final Iterator<LoggedRunsEntry> loggedRunsEntryIterator = loggedRuns.iterator();
		while (loggedRunsEntryIterator.hasNext()) {
			if (loggedRunsEntryIterator.next().getColor() == null) {
				loggedRunsEntryIterator.remove();
			}
		}
	}

}
