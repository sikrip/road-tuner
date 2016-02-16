package sikrip.roaddyno.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import sikrip.roaddyno.engine.DynoSimulator;
import sikrip.roaddyno.logreader.EcuLogReader;
import sikrip.roaddyno.logreader.MegasquirtLogReader;
import sikrip.roaddyno.model.DynoSimulationResult;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.web.chart.ChartDataProvider;
import sikrip.roaddyno.web.chart.PlotColorProvider;
import sikrip.roaddyno.web.chart.UploadedRunInfo;

@Controller
@Scope("session")
public class RoadDynoController {

	public static final int TPS_START_THRESHOLD = 98;

	@Autowired
	private ObjectMapper objectMapper;

	private final List<DynoSimulationResult> simulationResults = new ArrayList<>();

	@RequestMapping("/")
	public String index() {
		simulationResults.clear();
		return "index";
	}

	@RequestMapping("/addrun")
	public String addRun(Model model) {
		model.addAttribute("runInfo", new UploadedRunInfo());
		return "add_run_form";
	}

	@RequestMapping(value = "/addrun", method = RequestMethod.POST)
	public String addRun(UploadedRunInfo runInfo, @RequestParam("file") MultipartFile file, Model model) {
		if (!file.isEmpty()) {
			try {
				EcuLogReader logReader = new MegasquirtLogReader();
				List<LogEntry> logEntries = logReader.readLog(file.getInputStream(), TPS_START_THRESHOLD);
				DynoSimulationResult result = DynoSimulator.run(logEntries, file.getOriginalFilename(),
						runInfo.getFinalGearRatio(), runInfo.getGearRatio(),
						runInfo.getTyreDiameter(), runInfo.getCarWeight(),
						runInfo.getOccupantsWeight(),
						runInfo.getFrontalArea(),
						runInfo.getCoefficientOfDrag());
				simulationResults.add(result);
			} catch (Exception e) {
				// TODO hanle
				return "error";
			}
		} else {
			// TODO hanle
			return "error";
		}
		return "redirect: /dynoplot";
	}

	@RequestMapping("/dynoplot")
	public String dynoPlot(Model model) {
		try {
			String chartDef = objectMapper.writeValueAsString(new ChartDataProvider().createJsonData(simulationResults, new PlotColorProvider()));
			model.addAttribute("chartDef", chartDef);

			List<UploadedRunInfo> runInfoList = new ArrayList<>();
			PlotColorProvider colorProvider = new PlotColorProvider();
			for (DynoSimulationResult simulationResult : simulationResults) {
				runInfoList.add(new UploadedRunInfo(simulationResult, colorProvider.pop()));
			}
			model.addAttribute("runInfoList", runInfoList);

			return "dynoplot";
		} catch (JsonProcessingException e) {
			// TODO hanle
			return "error";
		}
	}

}
