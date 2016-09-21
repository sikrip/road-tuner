package sikrip.roaddyno.web.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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

import sikrip.roaddyno.engine.DynoSimulator;
import sikrip.roaddyno.eculogreader.EcuLogReader;
import sikrip.roaddyno.eculogreader.MegasquirtLogReader;
import sikrip.roaddyno.engine.DynoSimulationResult;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.web.chart.ChartDataProvider;
import sikrip.roaddyno.web.chart.PlotColorProvider;
import sikrip.roaddyno.web.chart.UploadedRun;

@Controller
@Scope("session")
public class RoadDynoController {

	public static final String ERROR_TEXT_KEY = "errorTxt";
	protected final Logger LOGGER = LoggerFactory.getLogger(RoadDynoController.class);

	public static final int TPS_START_THRESHOLD = 95;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private SessionVehicleData vehicleData;

	private final List<UploadedRun> uploadedRuns = new ArrayList<>();
	private final PlotColorProvider colorProvider = new PlotColorProvider();

	@RequestMapping("/")
	public String index() {
		return "index";
	}

	@RequestMapping("/addrun")
	public String addRun(Model model) {
		model.addAttribute("nav", "onlinedyno");
		model.addAttribute("runInfo", new UploadedRun().fromVehicleData(vehicleData));
		return "add-run-form";
	}

	@RequestMapping("/clearall")
	public String clearAll(Model model) {
		uploadedRuns.clear();
		colorProvider.reset();
		model.addAttribute("nav", "onlinedyno");
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

	@RequestMapping(value = "/addrun", method = RequestMethod.POST)
	public String addRun(UploadedRun runInfo, @RequestParam("file") MultipartFile file, Model model) {
		if (!file.isEmpty()) {
			try {
				EcuLogReader logReader = new MegasquirtLogReader();
				List<LogEntry> logEntries = logReader.readLog(file.getInputStream(), TPS_START_THRESHOLD);
				DynoSimulationResult result = DynoSimulator.runByRPM(logEntries,
						runInfo.getFinalGearRatio(),
						runInfo.getGearRatio(),
						runInfo.getTyreDiameter(),
						runInfo.getCarWeight(),
						runInfo.getOccupantsWeight(),
						runInfo.getFrontalArea(),
						runInfo.getCoefficientOfDrag());
				runInfo.setName(file.getOriginalFilename());
				runInfo.setResult(result);
				runInfo.setColor(colorProvider.pop());

				uploadedRuns.add(runInfo);
				vehicleData.updateFromRunInfo(runInfo);
			} catch (Exception e) {
				LOGGER.error("Could not add run.", e);
				model.addAttribute(ERROR_TEXT_KEY, e.getMessage());
				return "error";
			}
		} else {
			LOGGER.error("Could not add run, uploaded file is empty");
			model.addAttribute(ERROR_TEXT_KEY, "Could not add run, uploaded file is empty.");
			return "error";
		}
		return "redirect:/onlinedyno";
	}

	@RequestMapping(value = "/updaterun", method = RequestMethod.POST)
	public String updateRun(UploadedRun updatedRunInfo, Model model) {

		Optional<UploadedRun> existingRunInfo = uploadedRuns.stream().filter(r -> r.equals(updatedRunInfo)).findFirst();

		if (existingRunInfo.isPresent()) {
			existingRunInfo.get().setFinalGearRatio(updatedRunInfo.getFinalGearRatio());
			existingRunInfo.get().setGearRatio(updatedRunInfo.getGearRatio());
			existingRunInfo.get().setTyreDiameter(updatedRunInfo.getTyreDiameter());
			existingRunInfo.get().setCarWeight(updatedRunInfo.getCarWeight());
			existingRunInfo.get().setOccupantsWeight(updatedRunInfo.getOccupantsWeight());
			existingRunInfo.get().setFrontalArea(updatedRunInfo.getFrontalArea());
			existingRunInfo.get().setCoefficientOfDrag(updatedRunInfo.getCoefficientOfDrag());

			try {
				DynoSimulationResult result = DynoSimulator.runByRPM(existingRunInfo.get().getResult().getLogEntries(),
						existingRunInfo.get().getFinalGearRatio(),
						existingRunInfo.get().getGearRatio(),
						existingRunInfo.get().getTyreDiameter(),
						existingRunInfo.get().getCarWeight(),
						existingRunInfo.get().getOccupantsWeight(),
						existingRunInfo.get().getFrontalArea(),
						existingRunInfo.get().getCoefficientOfDrag());
				existingRunInfo.get().setResult(result);
				vehicleData.updateFromRunInfo(updatedRunInfo);

				return "redirect:/onlinedyno";
			} catch (Exception e) {
				LOGGER.error("Could not update run.", e);
				model.addAttribute(ERROR_TEXT_KEY, e.getMessage());
				return "error";
			}
		} else {
			String error = "Could not update run. Run with id " + updatedRunInfo.getId() + " was not found";
			LOGGER.error(error);
			model.addAttribute(ERROR_TEXT_KEY, error);
			return "error";
		}
	}

	@RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
	public String changeStatus(String rid, Boolean active, Model model) {
		Optional<UploadedRun> existingRunInfo = uploadedRuns.stream().filter(r -> r.getId().equals(rid)).findFirst();
		if (existingRunInfo.isPresent()) {
			existingRunInfo.get().setActive(active == null ? false : active);
			return "redirect:/onlinedyno";
		}
		String error = "Could not change statue of run with id " + rid + ". Run not found.";
		LOGGER.error(error);
		model.addAttribute(ERROR_TEXT_KEY, error);
		return "error";
	}

	@RequestMapping("/onlinedyno")
	public String onlineDyno(Model model) {
		if (uploadedRuns.isEmpty()) {
			return "redirect:/clearall";
		}
		try {
			List<UploadedRun> activeRuns = uploadedRuns.stream().filter(UploadedRun::isActive).collect(Collectors.toList());
			String chartDef = objectMapper.writeValueAsString(new ChartDataProvider().createMainChartDefinition(activeRuns));
			String auxChartDef = objectMapper.writeValueAsString(new ChartDataProvider().createAuxuliaryChartDefinition(activeRuns, "AFR"));

			model.addAttribute("chartDef", chartDef);
			model.addAttribute("auxChartDef", auxChartDef);
			model.addAttribute("runInfoList", uploadedRuns);
			model.addAttribute("nav", "onlinedyno");

			return "online-dyno-plot";
		} catch (JsonProcessingException e) {
			LOGGER.error("Could not plot runs.", e);
			model.addAttribute(ERROR_TEXT_KEY, e.getMessage());
			return "error";
		}
	}

	@RequestMapping("edit/{id}")
	public String edit(@PathVariable String id, Model model) {
		Optional<UploadedRun> runInfo = uploadedRuns.stream().filter(r -> id.equals(r.getId())).findFirst();
		if (runInfo.isPresent()) {
			model.addAttribute("runInfo", runInfo.get());
			model.addAttribute("nav", "onlinedyno");
			return "update-run-form";
		}
		String error = "Could not edit run with id " + id + ". Run not found.";
		LOGGER.error(error);
		model.addAttribute(ERROR_TEXT_KEY, error);
		return "error";
	}

	@RequestMapping("remove/{id}")
	public String remove(@PathVariable String id) {
		Iterator<UploadedRun> resultIterator = uploadedRuns.iterator();
		while (resultIterator.hasNext()) {
			if (resultIterator.next().getId().equals(id)) {
				resultIterator.remove();
				break;
			}
		}
		return "redirect:/onlinedyno";
	}

}
