package sikrip.roaddyno.web.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sikrip.roaddyno.engine.DynoSimulationResult;
import sikrip.roaddyno.engine.DynoSimulator;
import sikrip.roaddyno.web.chart.ChartDataProvider;
import sikrip.roaddyno.web.chart.PlotColorProvider;
import sikrip.roaddyno.web.logger.LogFileReader;
import sikrip.roaddyno.web.model.LogFileData;
import sikrip.roaddyno.web.model.LoggedRunsEntry;

@Controller
@Scope("session")
public class RoadDynoController {

	private static final String ERROR_TEXT_KEY = "errorTxt";
	public static final int MAX_FILE_BYTES = 4 * 1024 * 1024; // 4 mb

	private final Logger LOGGER = LoggerFactory.getLogger(RoadDynoController.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private SessionVehicleData vehicleData;

	private final Set<LoggedRunsEntry> logRunEntries = new TreeSet<>();

	private final PlotColorProvider colorProvider = new PlotColorProvider();

	@RequestMapping("/")
	public String index() {
		if(logRunEntries.isEmpty()) {
			return "index";
		}else {
			return "redirect:/online-dyno";
		}
	}

	@RequestMapping("/add")
	public String add(Model model) {
		if (logRunEntries.size() < colorProvider.size()) {
			model.addAttribute("nav", "onlinedyno");
			model.addAttribute("runInfo", new LoggedRunsEntry());
			return "select-log-file-form";
		} else {
			return showErrorPage(model, "Maximum number of plots reached.");
		}
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(LoggedRunsEntry runInfo, @RequestParam("file") MultipartFile file, Model model) {
		if (!file.isEmpty()) {
			if (file.getSize() <= MAX_FILE_BYTES) {
				try {
					LogFileData logFileData = LogFileReader.readLog(file);

					runInfo.setIndex(logRunEntries.size());
					runInfo.setSelectedAccelerationIdx(0);
					runInfo.setLogData(logFileData);
					runInfo.setName(file.getOriginalFilename());
					runInfo.updateVehicleData(vehicleData);

					logRunEntries.add(runInfo);

					// show update run form
					model.addAttribute("runInfo", runInfo);
					model.addAttribute("nav", "onlinedyno");

					return "update-run-form";
				} catch (Exception e) {
					return showErrorPage(model, "Could not add run", e);
				}
			} else {
				final String error = String.format("Maximum file size(%smb) exceeded, please select a smaller log file.", MAX_FILE_BYTES / 1024);
				return showErrorPage(model, error);
			}
		} else {
			return showErrorPage(model, "Could not add run, uploaded file is empty.");
		}
	}

	@RequestMapping("edit/{id}")
	public String edit(@PathVariable String id, Model model) {
		Optional<LoggedRunsEntry> runInfo = logRunEntries.stream().filter(r -> id.equals(r.getId())).findFirst();
		if (runInfo.isPresent()) {
			model.addAttribute("runInfo", runInfo.get());
			model.addAttribute("nav", "onlinedyno");
			return "update-run-form";
		}
		String error = "Could not edit run with id " + id + ". Run not found.";
		return showErrorPage(model, error);
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String edit(LoggedRunsEntry updatedRunInfo, Model model) {

		Optional<LoggedRunsEntry> existingEntryContainer = logRunEntries.stream().filter(r -> r.equals(updatedRunInfo)).findFirst();

		if (existingEntryContainer.isPresent()) {
			LoggedRunsEntry existingEntry = existingEntryContainer.get();

			existingEntry.setFinalGearRatio(updatedRunInfo.getFinalGearRatio());
			existingEntry.setGearRatio(updatedRunInfo.getGearRatio());
			existingEntry.setTyreDiameter(updatedRunInfo.getTyreDiameter());
			existingEntry.setCarWeight(updatedRunInfo.getCarWeight());
			existingEntry.setOccupantsWeight(updatedRunInfo.getOccupantsWeight());
			existingEntry.setFrontalArea(updatedRunInfo.getFrontalArea());
			existingEntry.setCoefficientOfDrag(updatedRunInfo.getCoefficientOfDrag());
			existingEntry.setSelectedAccelerationIdx(updatedRunInfo.getSelectedAccelerationIdx());

			if (existingEntry.getColor() == null) {
				// set color only the first time
				existingEntry.setColor(colorProvider.pop());
			}

			vehicleData.updateFromRunInfo(updatedRunInfo);

			try {
				// (re)run the dyno
				DynoSimulationResult dynoResult;
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

				return "redirect:/online-dyno";
			} catch (Exception e) {
				return showErrorPage(model, "Could not update run", e);
			}
		} else {
			String error = "Could not update run. Run with id " + updatedRunInfo.getId() + " was not found";
			return showErrorPage(model, error);
		}
	}

	@RequestMapping(value = "/change-status", method = RequestMethod.POST)
	public String changeStatus(String rid, Boolean active, Model model) {
		Optional<LoggedRunsEntry> existingRunInfo = logRunEntries.stream().filter(r -> r.getId().equals(rid)).findFirst();
		if (existingRunInfo.isPresent()) {
			existingRunInfo.get().setActive(active == null ? false : active);
			return "redirect:/online-dyno";
		}
		String error = "Could not change statue of run with id " + rid + ". Run not found.";
		return showErrorPage(model, error);
	}

	@RequestMapping("remove/{id}")
	public String remove(@PathVariable String id) {
		Iterator<LoggedRunsEntry> resultIterator = logRunEntries.iterator();
		while (resultIterator.hasNext()) {
			LoggedRunsEntry entry = resultIterator.next();
			if (entry.getId().equals(id)) {
				colorProvider.push(entry.getColor());
				resultIterator.remove();
				break;
			}
		}
		return "redirect:/online-dyno";
	}

	@RequestMapping("/online-dyno")
	public String onlineDyno(Model model) {
		clearInvalidEntries();
		if (logRunEntries.isEmpty()) {
			return "redirect:/clear-all";
		}
		try {
			final List<LoggedRunsEntry> activeRuns = logRunEntries.stream().filter(LoggedRunsEntry::isActive).collect(Collectors.toList());
			String chartDef = objectMapper.writeValueAsString(new ChartDataProvider().createMainChartDefinition(activeRuns));
			String auxChartDef = objectMapper.writeValueAsString(new ChartDataProvider().createAuxiliaryChartDefinition(activeRuns, "AFR"));
			model.addAttribute("chartDef", chartDef);
			model.addAttribute("auxChartDef", auxChartDef);
		} catch (JsonProcessingException e) {
			return showErrorPage(model, "Could not plot runs", e);
		}
		model.addAttribute("runInfoList", logRunEntries);
		model.addAttribute("nav", "onlinedyno");
		return "online-dyno-plot";
	}

	@RequestMapping("/clear-all")
	public String clearAll(Model model) {
		logRunEntries.clear();
		colorProvider.reset();
		model.addAttribute("nav", "onlinedyno");
		//TODO clear vehicleData?
		return "online-dyno-empty";
	}

	@RequestMapping("/help")
	public String help(Model model) {
		model.addAttribute("nav", "help");
		return "help";
	}

	@RequestMapping("/tsdyno")
	public String tsDyno(Model model) {
		model.addAttribute("nav", "tsdyno");
		return "tsdyno";
	}

	@RequestMapping("/contact")
	public String contact(Model model) {
		model.addAttribute("nav", "contact");
		return "contact";
	}

	private String showErrorPage(Model model, String error) {
		LOGGER.error(error);
		model.addAttribute(ERROR_TEXT_KEY, error);
		return "error";
	}

	private String showErrorPage(Model model, String message, Exception e) {
		String error = message;
		if (e == null || e.getMessage() == null) {
			error += ": Unexpected error occurred.";
		} else {
			error += ": " + e.getMessage();
		}
		LOGGER.error(error, e);
		model.addAttribute(ERROR_TEXT_KEY, error);
		return "error";
	}

	private void clearInvalidEntries() {
		Iterator<LoggedRunsEntry> loggedRunsEntryIterator = logRunEntries.iterator();
		while (loggedRunsEntryIterator.hasNext()) {
			if (loggedRunsEntryIterator.next().getColor() == null) {
				loggedRunsEntryIterator.remove();
			}
		}
	}
}
