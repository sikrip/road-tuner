package sikrip.roaddyno.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import sikrip.roaddyno.engine.DynoSimulator;
import sikrip.roaddyno.logreader.EcuLogReader;
import sikrip.roaddyno.logreader.MegasquirtLogReader;
import sikrip.roaddyno.model.DynoSimulationResult;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.web.model.ChartJsonData;
import sikrip.roaddyno.web.model.UploadedRunInfo;

@Controller
public class RoadDynoController {

	public static final int TPS_START_THRESHOLD = 98;
	@Autowired
	private ObjectMapper objectMapper;

	@RequestMapping("/")
	public String index(Model model) {
		return "index";
	}

	@RequestMapping("/newrun")
	public String newRun(Model model) {
		model.addAttribute("runInfo", new UploadedRunInfo());
		return "newrun";
	}

	@RequestMapping(value = "/dynoplot", method = RequestMethod.POST)
	public String loadRun(UploadedRunInfo runInfo, @RequestParam("file") MultipartFile file, Model model) {
		String chartDef = "{}";
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
				chartDef = objectMapper.writeValueAsString(new ChartJsonData().createJsonData(result));
			} catch (Exception e) {
				// TODO hanle
				return "error";
			}
		} else {
			// TODO hanle
			return "error";
		}
		model.addAttribute("chartDef", chartDef);
		return "dynoplot";
	}

}
