package sikrip.roaddyno.web.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

	public static final int TPS_START_THRESHOLD = 98;

	@Autowired
	private ObjectMapper objectMapper;

	private final List<UploadedRun> uploadedRuns = new ArrayList<>();
	private final PlotColorProvider colorProvider = new PlotColorProvider();

	@RequestMapping("/")
	public String index() {
		uploadedRuns.clear();
		colorProvider.reset();
		return "index";
	}

	@RequestMapping("/addrun")
	public String addRun(Model model) {
		model.addAttribute("runInfo", new UploadedRun());
		return "add-run-form";
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
			} catch (Exception e) {
				// TODO hanle
				return "error";
			}
		} else {
			// TODO hanle
			return "error";
		}
		return "redirect:/dynoplot";
	}

	@RequestMapping(value = "/updaterun", method = RequestMethod.POST)
	public String updateRun(UploadedRun updatedRunInfo) {

		UploadedRun existingRunInfo = null;
		for (UploadedRun uploadedRun : uploadedRuns) {
			if (uploadedRun.getId().equals(updatedRunInfo.getId())) {
				existingRunInfo = uploadedRun;
			}
		}

		if (existingRunInfo != null) {
			existingRunInfo.setFinalGearRatio(updatedRunInfo.getFinalGearRatio());
			existingRunInfo.setGearRatio(updatedRunInfo.getGearRatio());
			existingRunInfo.setTyreDiameter(updatedRunInfo.getTyreDiameter());
			existingRunInfo.setCarWeight(updatedRunInfo.getCarWeight());
			existingRunInfo.setOccupantsWeight(updatedRunInfo.getOccupantsWeight());
			existingRunInfo.setFrontalArea(updatedRunInfo.getFrontalArea());
			existingRunInfo.setCoefficientOfDrag(updatedRunInfo.getCoefficientOfDrag());

			try {
				DynoSimulationResult result = DynoSimulator.run(existingRunInfo.getResult().getLogEntries(),
						existingRunInfo.getFinalGearRatio(),
						existingRunInfo.getGearRatio(),
						existingRunInfo.getTyreDiameter(),
						existingRunInfo.getCarWeight(),
						existingRunInfo.getOccupantsWeight(),
						existingRunInfo.getFrontalArea(),
						existingRunInfo.getCoefficientOfDrag());
				existingRunInfo.setResult(result);
			} catch (InvalidSimulationParameterException e) {
				//TODO handle
				return "error";
			}

			return "redirect:/dynoplot";
		} else {
			//TODO handle
			return "error";
		}

	}

	@RequestMapping("/dynoplot")
	public String dynoPlot(Model model) {
		try {
			String chartDef = objectMapper.writeValueAsString(new ChartDataProvider().createJsonData(uploadedRuns));

			model.addAttribute("chartDef", chartDef);
			model.addAttribute("runInfoList", uploadedRuns);

			return "dyno-plot";
		} catch (JsonProcessingException e) {
			// TODO hanle
			return "error";
		}
	}

	@RequestMapping("edit/{id}")
	public String edit(@PathVariable String id, Model model) {

		UploadedRun runInfo = null;
		for (UploadedRun uploadedRun : uploadedRuns) {
			if (uploadedRun.getId().equals(id)) {
				runInfo = uploadedRun;
			}
		}

		if (runInfo != null) {
			model.addAttribute("runInfo", runInfo);
			return "update-run-form";
		}
		// TODO hanle
		return "error";
	}

	@RequestMapping("remove/{id}")
	public String delete(@PathVariable String id) {
		Iterator<UploadedRun> resultIterator = uploadedRuns.iterator();
		while (resultIterator.hasNext()) {
			if (resultIterator.next().getId().equals(id)) {
				resultIterator.remove();
			}
		}
		if (uploadedRuns.isEmpty()) {
			return "redirect:/";
		}
		return "redirect:/dynoplot";
	}

}
