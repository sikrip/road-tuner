package sikrip.roaddyno.web.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import sikrip.roaddyno.engine.InvalidSimulationParameterException;
import sikrip.roaddyno.logreader.EcuLogReader;
import sikrip.roaddyno.logreader.MegasquirtLogReader;
import sikrip.roaddyno.model.DynoSimulationResult;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.web.chart.ChartDataProvider;
import sikrip.roaddyno.web.chart.PlotColorProvider;
import sikrip.roaddyno.web.chart.UploadedRun;

@Controller
@Scope("session")
public class RoadDynoController {

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
	public String addRun(UploadedRun runInfo, @RequestParam("file") MultipartFile file) {
		if (!file.isEmpty()) {
			try {
				EcuLogReader logReader = new MegasquirtLogReader();
				List<LogEntry> logEntries = logReader.readLog(file.getInputStream(), TPS_START_THRESHOLD);
				DynoSimulationResult result = DynoSimulator.run(logEntries,
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
				// TODO hanle
				return "error";
			}
		} else {
			// TODO hanle
			return "error";
		}
		return "redirect:/onlinedyno";
	}

	@RequestMapping(value = "/updaterun", method = RequestMethod.POST)
	public String updateRun(UploadedRun updatedRunInfo) {

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
				DynoSimulationResult result = DynoSimulator.run(existingRunInfo.get().getResult().getLogEntries(),
						existingRunInfo.get().getFinalGearRatio(),
						existingRunInfo.get().getGearRatio(),
						existingRunInfo.get().getTyreDiameter(),
						existingRunInfo.get().getCarWeight(),
						existingRunInfo.get().getOccupantsWeight(),
						existingRunInfo.get().getFrontalArea(),
						existingRunInfo.get().getCoefficientOfDrag());
				existingRunInfo.get().setResult(result);
				vehicleData.updateFromRunInfo(updatedRunInfo);
			} catch (InvalidSimulationParameterException e) {
				//TODO handle
				return "error";
			}

			return "redirect:/onlinedyno";
		} else {
			//TODO handle
			return "error";
		}

	}

	@RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
	public String changeStatus(String rid, Boolean active) {
		Optional<UploadedRun> existingRunInfo = uploadedRuns.stream().filter(r -> r.getId().equals(rid)).findFirst();
		if (existingRunInfo.isPresent()) {
			existingRunInfo.get().setActive(active == null ? false : active);
			return "redirect:/onlinedyno";
		}
		//TODO handle
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
			// TODO hanle
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
		// TODO handle
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
