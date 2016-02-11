package sikrip.roaddyno.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import sikrip.roaddyno.engine.DynoSimulator;
import sikrip.roaddyno.logreader.EcuLogReader;
import sikrip.roaddyno.logreader.MegasquirtLogReader;
import sikrip.roaddyno.model.DynoSimulationResult;
import sikrip.roaddyno.model.LogEntry;
import sikrip.roaddyno.web.chartmodel.ChartJsonData;

import java.io.IOException;
import java.util.List;

@Controller
public class HomeController {

	@Autowired
	private ObjectMapper objectMapper;

	@RequestMapping("/")
	public String index(Model model) {
		String chartDef;
		EcuLogReader msLogReader = new MegasquirtLogReader();
		try {
			List<LogEntry> logEntries = msLogReader.readLog("/home/sikrip/Downloads/2016-01-18_22.30.22.msl", 96);
			DynoSimulationResult result = DynoSimulator.run(logEntries, "AW11 Rotrex", 4.312, 1.310, 528, 905, 85, 1.8, 0.34);
			chartDef = objectMapper.writeValueAsString(new ChartJsonData().createJsonData(result));
		} catch (IOException e) {
			chartDef="{}";
		}
		model.addAttribute("chartDef", chartDef);
		return "index";
	}

}
